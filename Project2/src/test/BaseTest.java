package test;

import gitlet.FileWriterFactory;
import gitlet.Gitlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;

public class BaseTest {
	protected static final String GITLET_DIR = ".gitlet/";
	protected static final String TESTING_DIR = "test_files/";

	/* matches either unix/mac or windows line separators */
	protected static final String LINE_SEPARATOR = "\r\n|[\r\n]";

	protected boolean stripWarning;
	protected boolean stripNewLines = true;
	protected boolean captureStreams;
	protected boolean echoStreams;
	protected boolean reuseInput;
	private List<String> createdFiles;
	private String stdinInput;

	protected PrintStream originalOut = System.out;
	protected PrintStream originalErr = System.err;
	protected InputStream originalIn = System.in;
	private InputStream fakeInput;

	protected String nl = System.getProperty("line.separator");

	/**
	 * Deletes existing gitlet system, resets the folder that stores files used
	 * in testing.
	 * 
	 * This method runs before every @Test method. This is important to enforce
	 * that all tests are independent and do not interact with one another.
	 */
	@Before
	public void setUp() {
		File f = new File(GITLET_DIR);
		if (f.exists()) {
			recursiveDelete(f);
		}
		f = new File(TESTING_DIR);
		if (f.exists()) {
			recursiveDelete(f);
		}
		f.mkdirs();

		createdFiles = new ArrayList<String>();
		stripWarning = true;
		stripNewLines = true;
		captureStreams = true;
		echoStreams = false;
		defaultInput();
	}

	public void defaultInput() {
		List<String> answers = new ArrayList<String>();
		answers.add("yes");
		setStdinInputText(answers, true);
	}

	@After
	public void tearDown() {
		File f = new File(GITLET_DIR);
		if (f.exists()) {
			recursiveDelete(f);
		}
		f = new File(TESTING_DIR);
		if (f.exists()) {
			recursiveDelete(f);
		}

		for (String name : createdFiles) {
			checkAndDelete(name);
		}

		restoreStreams();
		defaultInput();
		FileWriterFactory.useDefault();
	}

	protected void checkAndDelete(String name) {
		File f = new File(name);
		if (f.exists())
			recursiveDelete(f);
	}

	/**
	 * Convenience method for calling Gitlet's main. Anything that is printed
	 * out during this call to main will NOT actually be printed out, but will
	 * instead be returned as a string from this method.
	 * 
	 * Prepares a 'yes' answer on System.in so as to automatically pass through
	 * dangerous commands.
	 * 
	 * The '...' syntax allows you to pass in an arbitrary number of String
	 * arguments, which are packaged into a String[].
	 */
	// returns just Stdout as a string
	protected String gitlet(String... args) {
		return gitletErr(args)[0];
	}

	// returns Stdout and Stderr in a string array
	protected String[] gitletErr(String... args) {
		ByteArrayOutputStream[] streams = null;
		if (captureStreams)
			streams = captureStreamsAsStrings();

		try {
			/* Calls the main method using the input arguments. */
			Gitlet.main(args);
		} finally {
			/*
			 * Restores System.out and System.in (So you can print normally and
			 * take user input normally again).
			 */
			if (captureStreams)
				restoreStreams();
		}

		return getStreamText(streams);
	}

	protected ByteArrayOutputStream[] captureStreamsAsStrings() {
		/*
		 * Below we change System.out, so that when you call
		 * System.out.println(), it won't print to the screen, but will instead
		 * be added to the printingResults object.
		 */
		ByteArrayOutputStream printingResults = new ByteArrayOutputStream();
		ByteArrayOutputStream errorResults = new ByteArrayOutputStream();
		System.setOut(new PrintStream(printingResults));
		System.setErr(new PrintStream(errorResults));

		/*
		 * Prepares the answer "yes" on System.In, to pretend as if a user will
		 * type "yes". You won't be able to take user input during this time.
		 */
		// String answer = stdinInput;
		// InputStream is = new ByteArrayInputStream(answer.getBytes());
		// System.setIn(is);
		System.setIn(fakeInput);

		return new ByteArrayOutputStream[] { printingResults, errorResults };
	}

	protected void restoreStreams() {
		System.setOut(originalOut);
		System.setIn(originalIn);
		System.setErr(originalErr);
	}

	protected String[] getStreamText(ByteArrayOutputStream[] streams) {

		if (streams == null)
			return new String[] { "", "" };

		// return the string array, stripping out the newlines to make
		// assertions simpler
		String stdout = streams[0].toString();
		String stderr = streams[1].toString();

		if (stripNewLines) {
			stdout = stdout.replace("\n", "").replace("\r", "");
			stderr = stderr.replace("\n", "").replace("\r", "");
		}

		if (stripWarning) {
			stdout = stdout
					.replace(
							"Warning: The command you entered may alter the files "
									+ "in your working directory. Uncommitted changes may be lost. "
									+ "Are you sure you want to continue? (yes/no)",
							"");
		}

		if (echoStreams) {
			System.out.println(stdout);
			System.err.println(stderr);
		}

		return new String[] { stdout, stderr };
	}

	/**
	 * Returns the text from a standard text file (won't work with special
	 * characters).
	 */
	protected String getText(String fileName) {
		String _filename = fileName;// TESTING_DIR + fileName;
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(_filename));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * Creates a new file with the given fileName and gives it the text
	 * fileText.
	 */
	protected File createFile(String fileName, String fileText) {
		String _filename = fileName;// TESTING_DIR + fileName;
		createdFiles.add(fileName);

		File f = new File(_filename);
		if (!f.exists()) {
			try {
				f.createNewFile();
				f.setLastModified(System.currentTimeMillis() * 1000);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		writeFile(_filename, fileText);
		return f;
	}

	/**
	 * Creates a new directory with the given name
	 */
	protected File createDirectory(String name) {
		String _name = name;// TESTING_DIR + name;
		createdFiles.add(name);

		File f = new File(_name);
		if (!f.exists()) {
			f.mkdirs();
		}

		return f;
	}

	/**
	 * Creates a new directory with the given name
	 */
	protected void deleteDirectory(String name) {
		String _name = name;// TESTING_DIR + name;

		File f = new File(_name);
		if (f.exists()) {
			recursiveDelete(f);
		}
	}

	/**
	 * Replaces all text in the existing file with the given text.
	 */
	private void writeFile(String fileName, String fileText) {
		FileWriter fw = null;
		try {
			File f = new File(fileName);
			fw = new FileWriter(f, false);
			fw.write(fileText);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Deletes the file and all files inside it, if it is a directory.
	 */
	protected void recursiveDelete(File d) {
		if (d.isDirectory()) {
			for (File f : d.listFiles()) {
				recursiveDelete(f);
			}
		}
		d.delete();
	}

	/**
	 * Returns an array of commit messages associated with what log has printed
	 * out.
	 */
	protected String[] extractCommitMessages(String logOutput) {
		String[] logChunks = logOutput.split("====");
		int numMessages = logChunks.length - 1;
		String[] messages = new String[numMessages];
		for (int i = 0; i < numMessages; i++) {
			// System.out.println(logChunks[i + 1]);
			String[] logLines = logChunks[i + 1].split(LINE_SEPARATOR);
			messages[i] = logLines[3];
		}
		return messages;
	}

	protected String getLastCommitId(String logOutput) {

		Pattern p = Pattern.compile("Commit ([\\d\\w]+)\\.");
		Matcher matcher = p.matcher(logOutput);
		try {
			matcher.find();
			return matcher.group(1);
		} catch (Exception ex) {
			return "";
		}
	}

	protected String emptyStatus = "=== Branches ===" + "*master" + ""
			+ "=== Staged Files ===" + "" + "=== Files Marked for Removal ===";

	public void setStdinInputText(List<String> stdinInput, boolean reuse) {
		// this.stdinInput = stdinInput;
		// this.fakeInput = new FilterInputStream(new
		// ByteArrayInputStream(this.stdinInput.getBytes())) {
		this.fakeInput = new TestStream(stdinInput, reuse);
	}

	public void setStdinInput(List<String> stdinInput) {
		setStdinInputText(stdinInput, false);
	}

	// public void setStdinInput(String inputString){
	// setStdinInputText(Arrays.asList(new String[]{inputString}), false);
	// }

	public class TestStream extends InputStream {

		private InputStream inputStream;
		private List<String> text;
		private boolean isClosed;
		private boolean reuse;
		private int currentTextIndex;

		public TestStream(List<String> text, boolean reuse) {
			this.text = text == null ? Arrays.asList(new String[]{"yes"}) : text;
			this.reuse = reuse;
			currentTextIndex = 0;
			inputStream = new ByteArrayInputStream(text.get(0).getBytes());
		}

		// when close is called, presumably by Scanner, the next string is
		// loaded into the inputStream
		// if it is marked reuse, then the first string in the array is just
		// reloaded
		@Override
		public void close() throws IOException {
			if (isClosed)
				return;

			isClosed = true;
			currentTextIndex++;

			if (reuse) {
				inputStream = new ByteArrayInputStream(text.get(0).getBytes());
			} else {
				if (currentTextIndex < text.size()) {
					inputStream = new ByteArrayInputStream(text.get(currentTextIndex).getBytes());
				} else {
					inputStream = new ByteArrayInputStream("list ran empty".getBytes());
				}
			}
		}

		@Override
		public int read() throws IOException {
			isClosed = false;
			return inputStream.read();
		}

		@Override
		public int read(byte[] b) throws IOException {
			isClosed = false;
			return inputStream.read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			isClosed = false;
			return inputStream.read(b, off, len);
		}

		@Override
		public long skip(long n) throws IOException {
			return inputStream.skip(n);
		}

		@Override
		public int available() throws IOException {
			return inputStream.available();
		}

		@Override
		public synchronized void mark(int readlimit) {
			inputStream.mark(readlimit);
		}

		@Override
		public synchronized void reset() throws IOException {
			inputStream.reset();
		}

		@Override
		public boolean markSupported() {
			return inputStream.markSupported();
		}
	}

}

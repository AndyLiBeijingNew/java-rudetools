package net.sf.rudetools.common.tools.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

class ShellStreamHandler extends Thread {
	protected InputStream inputStream;
	protected PrintWriter printWriter;
	protected StringBuffer output = new StringBuffer();

	ShellStreamHandler(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = br.readLine()) != null) {
				output.append(line).append("\n");
			}
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
			}
		}
	}

	StringBuffer getOutput() {
		return output;
	}

}

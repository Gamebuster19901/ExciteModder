package com.gamebuster19901.excite.modding;

import java.io.IOException;
import java.io.OutputStream;

public class MultiOutputStream extends OutputStream {

	private final OutputStream[] outputs;
	
	public MultiOutputStream(OutputStream... outputs) {
		this.outputs = outputs;
	}
	
	@Override
	public void write(int b) throws IOException {
		for(OutputStream out : outputs) {
			try {
				out.write(b);
			}
			catch(Throwable t) {
				//swallow, we can't log this or we will get a stackoverflow
			}
		}
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		for(OutputStream out : outputs) {
			try {
				out.write(b);
			}
			catch(Throwable t) {
				//swallow, we can't log this or we will get a stackoverflow
			}
		}
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for(OutputStream out : outputs) {
			try {
				out.write(b, off, len);
			}
			catch(Throwable t) {
				//swallow, we can't log this or we will get a stackoverflow
			}
		}
	}

	@Override
	public void flush() throws IOException {
		for(OutputStream out : outputs) {
			try {
				out.flush();
			}
			catch(Throwable t) {
				//swallow, we can't log this or we will get a stackoverflow
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		for(OutputStream out : outputs) {
			try {
				out.close();
			}
			catch(Throwable t) {
				//swallow, we can't log this or we will get a stackoverflow
			}
		}
	}
	
}

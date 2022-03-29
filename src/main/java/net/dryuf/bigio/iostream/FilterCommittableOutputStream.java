package net.dryuf.bigio.iostream;

import net.dryuf.bigio.Committable;

import java.io.IOException;
import java.io.OutputStream;


/**
 * {@link Committable} enabled {@link OutputStream}.
 */
public abstract class FilterCommittableOutputStream extends CommittableOutputStream
{
	private final OutputStream output;

	protected FilterCommittableOutputStream(OutputStream output)
	{
		this.output = output;
	}

	@Override
	public void write(int b) throws IOException
	{
		output.write(b);
	}

	@Override
	public void write(byte[] b, int o, int l) throws IOException
	{
		output.write(b, o, l);
	}

	@Override
	public void flush() throws IOException
	{
		output.flush();
	}

	@Override
	public void close() throws IOException
	{
		output.close();
	}
}

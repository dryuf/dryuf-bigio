package net.dryuf.bigio.iostream;

import net.dryuf.bigio.Committable;

import java.io.OutputStream;


/**
 * {@link Committable} enabled {@link OutputStream}.
 */
public abstract class CommittableOutputStream extends OutputStream implements Committable
{
}

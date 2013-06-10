/*
 * TCircularBuffer.java
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 * Copyright (c) 1999 by Matthias Pfisterer
 * Copyright (c) 2012 by fireandfuel from Cuina Team
 * (http://www.cuina.byethost12.com/)
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as published
 * by the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Library General Public License for more details.
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package tritonus;

public class TCircularBuffer {
	public static interface Trigger {
		public void execute();
	}
	
	private final boolean	m_bBlockingRead;
	private final boolean	m_bBlockingWrite;
	private final byte[]	m_abData;
	private final int		m_nSize;
	private long			m_lReadPos;
	private long			m_lWritePos;
	private final Trigger	m_trigger;
	
	private boolean			m_bOpen;
	
	public TCircularBuffer(int nSize, boolean bBlockingRead,
			boolean bBlockingWrite, Trigger trigger) {
		m_bBlockingRead = bBlockingRead;
		m_bBlockingWrite = bBlockingWrite;
		m_nSize = nSize;
		m_abData = new byte[m_nSize];
		m_lReadPos = 0;
		m_lWritePos = 0;
		m_trigger = trigger;
		m_bOpen = true;
	}
	
	public int availableRead() {
		return (int) (m_lWritePos - m_lReadPos);
	}
	
	public int availableWrite() {
		return m_nSize - availableRead();
	}
	
	public void close() {
		m_bOpen = false;
		// TODO: call notify() ?
	}
	
	private int getReadPos() {
		return (int) (m_lReadPos % m_nSize);
	}
	
	private int getWritePos() {
		return (int) (m_lWritePos % m_nSize);
	}
	
	private boolean isOpen() {
		return m_bOpen;
	}
	
	public int read(byte[] abData) {
		return read(abData, 0, abData.length);
	}
	
	public int read(byte[] abData, int nOffset, int nLength) {
		
		if (!isOpen())
			if (availableRead() > 0) nLength = Math.min(nLength,
					availableRead());
			else return -1;
		synchronized (this) {
			if (m_trigger != null && availableRead() < nLength)
				m_trigger.execute();
			if (!m_bBlockingRead) nLength = Math.min(availableRead(), nLength);
			int nRemainingBytes = nLength;
			while (nRemainingBytes > 0) {
				while (availableRead() == 0)
					try {
						wait();
					}
					catch (final InterruptedException e) {
						
					}
				int nAvailable = Math.min(availableRead(), nRemainingBytes);
				while (nAvailable > 0) {
					final int nToRead = Math.min(nAvailable, m_nSize
							- getReadPos());
					System.arraycopy(m_abData, getReadPos(), abData, nOffset,
							nToRead);
					m_lReadPos += nToRead;
					nOffset += nToRead;
					nAvailable -= nToRead;
					nRemainingBytes -= nToRead;
				}
				notifyAll();
			}
			
			return nLength;
		}
	}
	
	public int write(byte[] abData) {
		return write(abData, 0, abData.length);
	}
	
	public int write(byte[] abData, int nOffset, int nLength) {
		
		synchronized (this) {
			
			if (!m_bBlockingWrite)
				nLength = Math.min(availableWrite(), nLength);
			int nRemainingBytes = nLength;
			while (nRemainingBytes > 0) {
				while (availableWrite() == 0)
					try {
						wait();
					}
					catch (final InterruptedException e) {
						
					}
				int nAvailable = Math.min(availableWrite(), nRemainingBytes);
				while (nAvailable > 0) {
					final int nToWrite = Math.min(nAvailable, m_nSize
							- getWritePos());
					// TDebug.out("src buf size= " + abData.length +
					// ", offset = " + nOffset + ", dst buf size=" +
					// m_abData.length + " write pos=" + getWritePos() + " len="
					// + nToWrite);
					System.arraycopy(abData, nOffset, m_abData, getWritePos(),
							nToWrite);
					m_lWritePos += nToWrite;
					nOffset += nToWrite;
					nAvailable -= nToWrite;
					nRemainingBytes -= nToWrite;
				}
				notifyAll();
			}
			
			return nLength;
		}
	}
	
}

/*** TCircularBuffer.java ***/


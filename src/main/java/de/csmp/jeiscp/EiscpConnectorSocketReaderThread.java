package de.csmp.jeiscp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EiscpConnectorSocketReaderThread implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(EiscpConnectorSocketReaderThread.class);
	
	EiscpConnector conn;
	BufferedInputStream socketIn;
	/** use thread safe implementation of collection for supporting concurrent adding/removing of listener */
	Collection<EiscpListener> listenerList = new ConcurrentLinkedQueue<EiscpListener>();
	
	private volatile boolean quit = false;
	
	public EiscpConnectorSocketReaderThread(EiscpConnector conn, BufferedInputStream socketIn) {
		this.conn = conn;
		this.socketIn = socketIn;
	}

	@Override
	public void run() {
		byte[] response = new byte[4];

		while (! quit) {
			log.trace("readLoop");
			try {
				blockedReadQuadrupel(response);
				EiscpProtocolHelper.validateIscpSignature(response, 0);
				
				blockedReadQuadrupel(response);
				EiscpProtocolHelper.validateHeaderLengthSignature(response, 0);
				
				blockedReadQuadrupel(response);
				int messageSize = EiscpProtocolHelper.readMessageSize(response, 0);
				
				blockedReadQuadrupel(response);
				EiscpProtocolHelper.validateEiscpVersion(response, 0);
				
				// eISCP encapulation-header ends here - ISCP begins !1xxx
				
				byte[] iscpMessage = new byte[messageSize];
				for (int i=0; i<messageSize; i++) {
					iscpMessage[i] = (byte) socketIn.read();
				}
				
				String iscpResult = EiscpProtocolHelper.parseIscpMessage(iscpMessage);
				log.debug("receivedIscpCommand: " + iscpResult);
				try {
					fireReceivedIscpMessage(iscpResult);
				} catch (Throwable ex) {
					log.error("error in listener {}", ex.getMessage(), ex);
				}
			} catch (EiscpMessageFormatException ex) {
				log.warn(ex.getMessage() + " - " + EiscpProtocolHelper.convertToHexString(response));
				log.debug("skip bytes until EOF/CR");
				
				if (isEofMarkerfInArray(response)) {
					log.debug("found eof in response block");
				} else {
					boolean eofFound = false;
					try {
						while (! eofFound) {
							byte b = (byte) socketIn.read();
							if (b == -1) {
								log.debug("end of stream");
								quit();
								eofFound = true;
							} else {
								// log.debug("discard " + EiscpProtocolHelper.convertToHexString(new byte[]{b}));
								eofFound = EiscpProtocolHelper.isEofMarker(b);
							}
						};
						log.trace("found EOF");
					} catch (Exception ex2) {
						log.error("not handled", ex2);
					}
				}
			} catch (Exception ex) {
				log.warn(ex.getMessage());
				ex.printStackTrace();
				quit();
			}
		}
	}

	public void fireReceivedIscpMessage(String iscpResult) {
		for (EiscpListener listener : listenerList) {
			listener.receivedIscpMessage(iscpResult);	
		}
	}
	
	public void addListener(EiscpListener listener) {
		listenerList.add(listener);
	}
	
	public void removeListener(EiscpListener listener) {
		listenerList.remove(listener);
	}

	public boolean isEofMarkerfInArray(byte[] response) {
		boolean eofFound = false;
		for (int i=0; i<response.length; i++) {
			eofFound = eofFound || EiscpProtocolHelper.isEofMarker(response[i]);
		}
		return eofFound;
	}

	private void blockedReadQuadrupel(byte[] bb) throws IOException {
		bb[0] = (byte) socketIn.read();
		bb[1] = (byte) socketIn.read();
		bb[2] = (byte) socketIn.read();
		bb[3] = (byte) socketIn.read();
	}
	
	public void quit() {
		quit = true;
	}
}

package common;

/**
 * @author peng
 */
public interface MidiMessage {

	byte[] getData();

	byte[] toBytes();

	int getLength();
}
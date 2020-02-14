package twistedgate.bedrocked.client.gui;

import org.lwjgl.input.Keyboard;

public class GuiKeyUtils{
	private GuiKeyUtils(){}
	
	/** Checks if any of both the Ctrl-Keys and the Shift-keys are held */
	public static boolean isCtrlShiftDown(){
		return isShiftDown() && isCtrlDown();
	}
	
	/** Checks if any Shift-key is held */
	public static boolean isShiftDown(){
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
	
	/** Checks if any Ctrl-key is held */
	public static boolean isCtrlDown(){
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
	}
}

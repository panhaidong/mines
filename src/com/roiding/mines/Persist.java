package com.roiding.mines;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;

public class Persist {
	public static Serializable load(Context context, String fileName) {
		Serializable obj = null;

		try {
			FileInputStream file = context.openFileInput(fileName);
			ObjectInput input = new ObjectInputStream(file);
			try {
				obj = (Serializable) input.readObject();
			} finally {
				input.close();
			}
		} catch (FileNotFoundException ex) {
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	public static void store(Context context, Serializable obj, String fileName) {
		try {

			// use buffering
			FileOutputStream file = context.openFileOutput(fileName,
					Context.MODE_WORLD_WRITEABLE);
			ObjectOutput output = new ObjectOutputStream(file);
			try {
				output.writeObject(obj);
				output.flush();
			} finally {
				output.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

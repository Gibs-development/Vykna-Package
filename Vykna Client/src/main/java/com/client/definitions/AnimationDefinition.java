package com.client.definitions;

import com.client.Class36;
import com.client.Configuration;
import com.client.Stream;
import com.client.StreamLoader;
import com.client.definitions.custom.AnimationDefinitionCustom;
import com.google.common.collect.Lists;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class AnimationDefinition {

	public static void unpackConfig(StreamLoader streamLoader) {
		Stream stream = new Stream(streamLoader.getArchiveData("seq.dat"));
		int length = stream.readUShort();
		if (anims == null)
			//anims = new AnimationDefinition[length];
			anims = new AnimationDefinition[length+ 5000];
		for (int j = 0; j < length; j++) {
			if (anims[j] == null)
				anims[j] = new AnimationDefinition();
			anims[j].id = j;
			anims[j].readValues(stream);
			AnimationDefinitionCustom.custom(j, anims);

			if (Configuration.dumpAnimationData) {
				if (anims[j].frameLengths != null && anims[j].frameLengths.length > 0) {
					int sum = 0;
					for (int i = 0; i < anims[j].frameLengths.length; i++) {
						if (anims[j].frameLengths[i] < 100) {
							sum += anims[j].frameLengths[i];
						}
					}

					System.out.println(j + ":" + sum);
				}
			}
		}

		if (Configuration.dumpAnimationData) {
			System.out.println("Dumping animation lengths..");

			try (BufferedWriter writer = new BufferedWriter(new FileWriter("./temp/animation_lengths.cfg"))) {
				for (int j = 0; j < length; j++) {
					if (anims[j].frameLengths != null && anims[j].frameLengths.length > 0) {
						int sum = 0;
						for (int i = 0; i < anims[j].frameLengths.length; i++) {
							if (anims[j].frameLengths[i] < 100) {
								sum += anims[j].frameLengths[i];
							}
						}
						writer.write(j + ":" + sum);
						writer.newLine();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("Dumping animation sounds..");
			for (int j = 0; j < length; j++) {
				if (anims[j].frameSounds != null) {
					System.out.println(j +":" + Arrays.toString(anims[j].frameSounds));
				}
			}

			System.out.println("Dumping animation fields to /temp/animation_dump.txt");
			dump();
		}
	}

	public int getFrameSound(int frameIndex) {
		if (frameSounds != null && frameIndex < frameSounds.length && frameSounds[frameIndex] != 0) {
			return frameSounds[frameIndex];
		} else {
			return -1;
		}
	}

	public int method258(int i) {
		try {
			int j = frameLengths[i];
			if (j == 0) {
				Class36 class36 = Class36.forId(primaryFrameIds[i]);
				if (class36 != null)
					j = frameLengths[i] = class36.anInt636;
			}
			if (j == 0)
				j = 1;
			return j;
		} catch (Exception e) {
			System.err.println("Error in animation id: " + id);
			e.printStackTrace();
			return 0;
		}
	}

	private void readValues(Stream stream) {
		int i;
		while ((i = stream.readUnsignedByte()) != 0) {

			if (i == 1) {
				frameCount = stream.readUShort();
				primaryFrameIds = new int[frameCount];
				secondaryFrameIds = new int[frameCount];
				frameLengths = new int[frameCount];
				for (int j = 0; j < frameCount; j++)
					frameLengths[j] = stream.readUShort();

				for (int j = 0; j < frameCount; j++) {
					primaryFrameIds[j] = stream.readUShort();
					secondaryFrameIds[j] = -1;
				}
				for (int j = 0; j < frameCount; j++) {
					primaryFrameIds[j] += stream.readUShort() << 16;
					secondaryFrameIds[j] = -1;
				}
			} else if (i == 2)
				loopOffset = stream.readUShort();
			else if (i == 3) {
				int k = stream.readUnsignedByte();
				anIntArray357 = new int[k + 1];
				for (int l = 0; l < k; l++)
					anIntArray357[l] = stream.readUnsignedByte();
				anIntArray357[k] = 9999999;
			} else if (i == 4)
				stretches = true;
			else if (i == 5)
				priority = stream.readUnsignedByte();
			else if (i == 6)
				leftHandItemID = stream.readUShort();
			else if (i == 7)
				rightHandItemID = stream.readUShort();
			else if (i == 8)
				replayCount = stream.readUnsignedByte();
			else if (i == 9)
				precedenceAnimating = stream.readUnsignedByte();
			else if (i == 10)
				precedenceWalking = stream.readUnsignedByte();
			else if (i == 11)
				resetCycle = stream.readUnsignedByte();
			else if (i == 12) {
				int len = stream.readUnsignedByte();
				secondaryFrameIds = new int[len];
				for (int i2 = 0; i2 < len; i2++) {
					secondaryFrameIds[i2] = stream.readUShort();
				}

				for (int i2 = 0; i2 < len; i2++) {
					secondaryFrameIds[i2] += stream.readUShort() << 16;
				}
			} else if (i == 13) {
				int var3 = stream.readUnsignedByte();
				frameSounds = new int[var3];
				for (int var4 = 0; var4 < var3; ++var4)
				{
					frameSounds[var4] = stream.read24BitInt();
					if (0 != frameSounds[var4]) {
						int var6 = frameSounds[var4] >> 8;
						int var8 = frameSounds[var4] >> 4 & 7;
						int var9 = frameSounds[var4] & 15;
						frameSounds[var4] = var6;
					}
				}
			} else if (i == 127) {
				// Hidden
			} else System.out.println("Error unrecognised seq config code: " + i);
		}
		if (frameCount == 0) {
			frameCount = 1;
			primaryFrameIds = new int[1];
			primaryFrameIds[0] = -1;
			secondaryFrameIds = new int[1];
			secondaryFrameIds[0] = -1;
			frameLengths = new int[1];
			frameLengths[0] = -1;
		}
		if (precedenceAnimating == -1)
			if (anIntArray357 != null)
				precedenceAnimating = 2;
			else
				precedenceAnimating = 0;
		if (precedenceWalking == -1) {
			if (anIntArray357 != null) {
				precedenceWalking = 2;
				return;
			}
			precedenceWalking = 0;
		}
	}

	public AnimationDefinition() {
		loopOffset = -1;
		stretches = false;
		priority = 5;
		leftHandItemID = -1;
		rightHandItemID = -1;
		replayCount = 99;
		precedenceAnimating = -1;
		precedenceWalking = -1;
		resetCycle = 2;
	}

	public static AnimationDefinition anims[];
	public int id;
	public int frameCount;
	public int primaryFrameIds[];
	public int secondaryFrameIds[];
	public int frameSounds[];
	public int[] frameLengths;
	public int loopOffset;
	public int anIntArray357[];
	public boolean stretches;
	public int priority;
	public int leftHandItemID;
	public int rightHandItemID;
	public int replayCount;
	public int precedenceAnimating;
	public int precedenceWalking;
	public int resetCycle;

	public static void dump() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("./temp/animation_dump.txt"))) {
			for (int index = 0; index < anims.length; index++) {
				AnimationDefinition anim = anims[index];
				if (anim != null) {
					writer.write("\tcase " + index + ":");
					writer.newLine();
					writer.write("\t\tanim.anInt352 = " + anim.frameCount + ";");
					writer.newLine();
					writer.write("\t\tanim.anInt356 = " + anim.loopOffset + ";");
					writer.newLine();
					writer.write("\t\tanim.aBoolean358 = " + anim.stretches + ";");
					writer.newLine();
					writer.write("\t\tanim.anInt359 = " + anim.priority + ";");
					writer.newLine();
					writer.write("\t\tanim.anInt360 = " + anim.leftHandItemID + ";");
					writer.newLine();
					writer.write("\t\tanim.anInt361 = " + anim.rightHandItemID + ";");
					writer.newLine();
					writer.write("\t\tanim.anInt362 = " + anim.replayCount + ";");
					writer.newLine();
					writer.write("\t\tanim.anInt363 = " + anim.precedenceAnimating + ";");
					writer.newLine();
					writer.write("\t\tanim.anInt364 = " + anim.precedenceWalking + ";");
					writer.newLine();
					writer.write("\t\tanim.anInt352 = " + anim.frameCount + ";");
					writer.newLine();
					writeArray(writer, "anIntArray353", anim.primaryFrameIds);
					writeArray(writer, "anIntArray354", anim.secondaryFrameIds);
					writeArray(writer, "frameLengths", anim.frameLengths);
					writeArray(writer, "anIntArray357", anim.anIntArray357);
					writeArray(writer, "class36Ids", anim.getClass36Ids());
					writer.write("\t\tbreak;");
					writer.newLine();
					writer.newLine();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int[] getClass36Ids() {
		List<Integer> ids = Lists.newArrayList();
		for (int frameId : primaryFrameIds) {
			if (!ids.contains(Class36.getClass36Id(frameId))) {
				ids.add(Class36.getClass36Id(frameId));
			}
		}
		int[] idsArray = new int[ids.size()];
		for (int index = 0; index < idsArray.length; index++)
			idsArray[index] = ids.get(index);
		return idsArray;
	}

	private static void writeArray(BufferedWriter writer, String name, int[] array) throws IOException {
		writer.write("\t\tanim." + name + " = ");

		if (array == null) {
			writer.write("null;");
		} else {
			writer.write("new int[] {");
			for (int value : array) {
				writer.write(value + ", ");
			}
			writer.write("};");
		}

		writer.newLine();
	}
}
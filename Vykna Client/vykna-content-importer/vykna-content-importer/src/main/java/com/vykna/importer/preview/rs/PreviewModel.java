package com.vykna.importer.preview.rs;

public final class PreviewModel {

    public int verticesCount;
    public int trianglesCount;
    public int texturesCount;

    public int[] verticesX;
    public int[] verticesY;
    public int[] verticesZ;

    public int[] trianglesX;
    public int[] trianglesY;
    public int[] trianglesZ;

    // fields referenced by the loader (not needed for wireframe, but required to decode safely)
    public int[] vertexData;
    public int[] types;
    public byte[] face_render_priorities;
    public byte face_priority;
    public int[] alphas;
    public int[] triangleData;

    public short[] colors;
    public short[] materials;
    public byte[] textures;

    public byte[] textureTypes;
    public short[] texturesX, texturesY, texturesZ;

    public int[][] animayaGroups;
    public int[][] animayaScales;
}

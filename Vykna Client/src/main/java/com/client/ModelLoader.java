package com.client;

public class ModelLoader {
    private static final boolean DEBUG_667 = true;
    private static final boolean DEBUG_MODEL_VALIDATION = false;
    /**
     * 525/667-era "bitmask" model format (commonly ends with -1, -1).
     * This is NOT the same as your decodeType1/2/3 variants when the header flag byte > 1 (e.g. 0x0F).
     *
     * Decodes enough for in-game rendering: verts, faces, base colors, face types, priorities, alpha.
     * Consumes other streams (skins/tex ids/etc) so offsets remain correct.
     */
    public static void decodeType525(Model def, byte[] data) {
        Buffer b1 = new Buffer(data);
        Buffer b2 = new Buffer(data);
        Buffer b3 = new Buffer(data);
        Buffer b4 = new Buffer(data);
        Buffer b5 = new Buffer(data);
        Buffer b6 = new Buffer(data);
        Buffer b7 = new Buffer(data);

        // Header lives at end-23 in this family
        b1.setOffset(data.length - 23);

        int numVertices = b1.readUShort();
        int numFaces    = b1.readUShort();
        int numTexTris  = b1.readUnsignedByte();

        int flags       = b1.readUnsignedByte(); // bitmask (often > 1)
        boolean hasFaceTypes = (flags & 1) != 0;

        int priFlag     = b1.readUnsignedByte(); // 255 = per-face priorities, else constant
        int alphaFlag   = b1.readUnsignedByte(); // 1 = per-face alpha
        int triSkinFlag = b1.readUnsignedByte(); // 1 = per-face skin
        int texFlag     = b1.readUnsignedByte(); // 1 = per-face texture id
        int vtxSkinFlag = b1.readUnsignedByte(); // 1 = per-vertex skin

        int xDataLen       = b1.readUShort();
        int yDataLen       = b1.readUShort();
        int zDataLen       = b1.readUShort();
        int faceIdxDataLen = b1.readUShort();
        int miscDataLen    = b1.readUShort();
        if (miscDataLen == 0xFFFF) {
            miscDataLen = 0;
        }

        // Texture triangle types live at file start (numTexTris bytes).
        // We only need to consume them to keep offsets correct.
        if (numTexTris > 0) {
            def.textureTypes = new byte[numTexTris];
            b1.setOffset(0);
            for (int i = 0; i < numTexTris; i++) {
                def.textureTypes[i] = b1.readByte();
            }
        }

        // ---- stream layout ----
        int off = numTexTris;

        int vertexFlagsOff = off;              off += numVertices;

        int faceTypeOff = off;
        if (hasFaceTypes) off += numFaces;

        int faceOpcodeOff = off;               off += numFaces;

        int facePriorityOff = off;
        if (priFlag == 255) off += numFaces;

        int triSkinOff = off;
        if (triSkinFlag == 1) off += numFaces;

        int vtxSkinOff = off;
        if (vtxSkinFlag == 1) off += numVertices;

        int alphaOff = off;
        if (alphaFlag == 1) off += numFaces;

        int faceIdxDataOff = off;              off += faceIdxDataLen;

        int faceTexOff = off;
        if (texFlag == 1) off += numFaces * 2;

        int miscOff = off;                     off += miscDataLen;

        int faceColorOff = off;                off += numFaces * 2;

        int xDataOff = off;                    off += xDataLen;
        int yDataOff = off;                    off += yDataLen;
        int zDataOff = off;                    off += zDataLen;

        // ---- allocate ----
        def.verticesCount  = numVertices;
        def.trianglesCount = numFaces;
        def.texturesCount  = numTexTris;

        def.verticesX = new int[numVertices];
        def.verticesY = new int[numVertices];
        def.verticesZ = new int[numVertices];

        def.trianglesX = new int[numFaces];
        def.trianglesY = new int[numFaces];
        def.trianglesZ = new int[numFaces];

        def.colors = new short[numFaces];

        if (hasFaceTypes) def.types = new int[numFaces];
        if (alphaFlag == 1) def.alphas = new int[numFaces];

        // ✅ IMPORTANT: keep skin arrays so Model.method469() can build groups
        if (vtxSkinFlag == 1) def.vertexData = new int[numVertices];
        if (triSkinFlag == 1) def.triangleData = new int[numFaces];

        // ---- decode vertices (delta + smart) ----
        b1.setOffset(vertexFlagsOff);
        b2.setOffset(xDataOff);
        b3.setOffset(yDataOff);
        b4.setOffset(zDataOff);
        b5.setOffset(vtxSkinOff);

        int vx = 0, vy = 0, vz = 0;
        for (int v = 0; v < numVertices; v++) {
            int mask = b1.readUnsignedByte();

            int dx = 0;
            if ((mask & 1) != 0) dx = b2.readSmart();

            int dy = 0;
            if ((mask & 2) != 0) dy = b3.readSmart();

            int dz = 0;
            if ((mask & 4) != 0) dz = b4.readSmart();

            vx += dx; vy += dy; vz += dz;

            def.verticesX[v] = vx;
            def.verticesY[v] = vy;
            def.verticesZ[v] = vz;

            // ✅ store vertex skin group
            if (vtxSkinFlag == 1) {
                def.vertexData[v] = b5.readUnsignedByte();
            }
        }

        // ---- decode face attributes + colors ----
        b1.setOffset(faceColorOff);
        b2.setOffset(faceTypeOff);
        b3.setOffset(triSkinOff);
        b4.setOffset(alphaOff);
        b5.setOffset(facePriorityOff);
        b6.setOffset(faceTexOff);
        b7.setOffset(miscOff);

        for (int f = 0; f < numFaces; f++) {
            int col = b1.readUShort();

            if (hasFaceTypes) {
                int t = (int) b2.readByte(); // signed
                def.types[f] = t;

                // Classic behavior: type=2 indicates textured face; many clients set color=65535
                if (t == 2) col = 65535;
            }

            def.colors[f] = (short) col;

            if (priFlag == 255) {
                if (def.face_render_priorities == null) def.face_render_priorities = new byte[numFaces];
                def.face_render_priorities[f] = b5.readByte();
            } else {
                def.face_priority = (byte) priFlag;
            }

            if (alphaFlag == 1) {
                int a = (int) b4.readByte();
                if (a < 0) a = 256 + a;
                def.alphas[f] = a;
            }

            // ✅ store face skin group
            if (triSkinFlag == 1) {
                def.triangleData[f] = b3.readUnsignedByte();
            }

            if (texFlag == 1) {
                // consume per-face texture id (ignore for now)
                b6.readUShort();
            }
        }

        // ---- decode triangle indices ----
        b1.setOffset(faceIdxDataOff);
        b2.setOffset(faceOpcodeOff);

        int a = 0, b = 0, c = 0;
        int last = 0;

        for (int f = 0; f < numFaces; f++) {
            int opcode = b2.readUnsignedByte();

            if (opcode == 1) {
                a = b1.readSmart() + last; last = a;
                b = b1.readSmart() + last; last = b;
                c = b1.readSmart() + last; last = c;
            } else if (opcode == 2) {
                b = c;
                c = b1.readSmart() + last; last = c;
            } else if (opcode == 3) {
                a = c;
                c = b1.readSmart() + last; last = c;
            } else if (opcode == 4) {
                int tmp = a;
                a = b;
                b = tmp;
                c = b1.readSmart() + last; last = c;
            } else {
                throw new IllegalArgumentException("Bad face opcode " + opcode + " at face " + f);
            }

            def.trianglesX[f] = a;
            def.trianglesY[f] = b;
            def.trianglesZ[f] = c;
        }

        // ✅ optional: consume texture triangles if you later implement textured rendering.
        // For now we leave it out, since your renderer works without.

        if (!validateModelInvariants(def, def.getModelId(), hasFaceTypes, priFlag == 255)) {
            applyTextureCompatibilityFallback(def, def.getModelId(), "type525");
        }
    }


    public static void decode667(Model def, byte[] data) {
        if (DEBUG_667) {
            System.out.println("[667 decode] model=" + def.getModelId() + " bytes=" + (data == null ? 0 : data.length));
        }
        if (data == null || data.length < 3) {
            if (DEBUG_667) System.out.println("[667 decode] invalid data; skipping");
            return;
        }

        final int n = data.length;

        final int tailSubtype = data[n - 2]; // signed
        final int tailMarker  = data[n - 1]; // signed

        boolean looksLikeBitmask525 = false;
        if (tailSubtype == -1 && tailMarker == -1 && n >= 23) {
            int headerStart = n - 23;
            int flags = data[headerStart + 5] & 0xFF;
            looksLikeBitmask525 = flags > 1;
        }

        Decoder[] preferred;

        if (tailMarker == -1) {
            if (tailSubtype == -3) {
                preferred = new Decoder[]{ TYPE3 };
            } else if (tailSubtype == -2) {
                preferred = new Decoder[]{ TYPE2 };
            } else if (tailSubtype == -1) {
                preferred = looksLikeBitmask525
                        ? new Decoder[]{ TYPE525, TYPE1 }
                        : new Decoder[]{ TYPE1 };
            } else {
                preferred = new Decoder[]{ OLD_FORMAT };
            }
        } else {
            preferred = new Decoder[]{ OLD_FORMAT };
        }

        Decoder[] fallbacks = new Decoder[]{
                READ_622,
                TYPE3,
                TYPE525,
                TYPE2,
                TYPE1,
                OLD_FORMAT
        };

        // ✅ ONE decode attempt
        if (tryDecoders(def, data, preferred, fallbacks)) {

            if (!validateModelInvariants(def, def.getModelId(), def.types != null, def.face_priority == 255 || def.face_priority == -1)) {
                applyTextureCompatibilityFallback(def, def.getModelId(), "decode667");
            }

            // ✅ Build groups ONCE after successful decode
            try {
                def.method469();
            } catch (Throwable t) {
                if (DEBUG_667) System.out.println("[667 decode] method469 failed: " + t);
            }
            return;
        }

        if (DEBUG_667) {
            System.out.println("[667 decode] all decoders failed sanity checks for model " + def.getModelId()
                    + " tailSubtype=" + tailSubtype + " tailMarker=" + tailMarker
                    + " bitmask525=" + looksLikeBitmask525);
        }
    }



    private interface Decoder {
        String name();
        void decode(Model def, byte[] data);
    }

    private static final Decoder TYPE3 = new Decoder() {
        @Override
        public String name() {
            return "type3";
        }

        @Override
        public void decode(Model def, byte[] data) {
            ModelLoader.decodeType3(def, data);
        }
    };

    private static final Decoder TYPE2 = new Decoder() {
        @Override
        public String name() {
            return "type2";
        }

        @Override
        public void decode(Model def, byte[] data) {
            ModelLoader.decodeType2(def, data);
        }
    };

    private static final Decoder TYPE1 = new Decoder() {
        @Override
        public String name() {
            return "type1";
        }

        @Override
        public void decode(Model def, byte[] data) {
            ModelLoader.decodeType1(def, data);
        }
    };

    private static final Decoder OLD_FORMAT = new Decoder() {
        @Override
        public String name() {
            return "old";
        }

        @Override
        public void decode(Model def, byte[] data) {
            ModelLoader.decodeOldFormat(def, data);
        }
    };

    private static final Decoder READ_622 = new Decoder() {
        @Override
        public String name() {
            return "read622";
        }

        @Override
        public void decode(Model def, byte[] data) {
            def.read622Model(data, def.getModelId());
        }
    };

    private static boolean tryDecoders(Model def, byte[] data, Decoder[] preferred, Decoder[] fallbacks) {
        java.util.LinkedHashSet<String> tried = new java.util.LinkedHashSet<>();
        Decoder[] attempts = new Decoder[preferred.length + fallbacks.length];
        System.arraycopy(preferred, 0, attempts, 0, preferred.length);
        System.arraycopy(fallbacks, 0, attempts, preferred.length, fallbacks.length);

        for (Decoder decoder : attempts) {
            if (!tried.add(decoder.name())) {
                continue;
            }
            resetModel(def);
            try {
                decoder.decode(def, data);
            } catch (Exception ex) {
                if (DEBUG_667) {
                    System.out.println("[667 decode] " + decoder.name() + " threw: " + ex.getMessage());
                }
                continue;
            }
            if (isSane(def)) {
                if (DEBUG_667) {
                    System.out.println("[667 decode] succeeded with " + decoder.name());
                }
                return true;
            }
            if (DEBUG_667) {
                System.out.println("[667 decode] decoder " + decoder.name() + " produced invalid geometry");
            }
        }
        return false;
    }

    private static void resetModel(Model def) {
        def.verticesCount = 0;
        def.trianglesCount = 0;
        def.texturesCount = 0;
        def.verticesX = null;
        def.verticesY = null;
        def.verticesZ = null;
        def.trianglesX = null;
        def.trianglesY = null;
        def.trianglesZ = null;
        def.colorsX = null;
        def.colorsY = null;
        def.colorsZ = null;
        def.types = null;
        def.face_render_priorities = null;
        def.alphas = null;
        def.colors = null;
        def.face_priority = 0;
        def.texturesX = null;
        def.texturesY = null;
        def.texturesZ = null;
        def.vertexData = null;
        def.triangleData = null;
        def.vertexGroups = null;
        def.faceGroups = null;
        def.materials = null;
        def.textures = null;
        def.textureTypes = null;
        def.animayaGroups = null;
        def.animayaScales = null;
    }

    private static boolean isSane(Model def) {
        if (def.verticesCount <= 0 || def.trianglesCount <= 0) {
            return false;
        }
        if (def.verticesX == null || def.verticesY == null || def.verticesZ == null) {
            return false;
        }
        if (def.trianglesX == null || def.trianglesY == null || def.trianglesZ == null) {
            return false;
        }
        if (def.verticesX.length < def.verticesCount
            || def.verticesY.length < def.verticesCount
            || def.verticesZ.length < def.verticesCount) {
            return false;
        }
        if (def.trianglesX.length < def.trianglesCount
            || def.trianglesY.length < def.trianglesCount
            || def.trianglesZ.length < def.trianglesCount) {
            return false;
        }

        int vCount = def.verticesCount;
        int check = Math.min(def.trianglesCount, 2000);
        int bad = 0;
        for (int i = 0; i < check; i++) {
            int a = def.trianglesX[i];
            int b = def.trianglesY[i];
            int c = def.trianglesZ[i];
            if (a < 0 || a >= vCount || b < 0 || b >= vCount || c < 0 || c >= vCount) {
                bad++;
            }
        }
        if (bad > check / 4) {
            return false;
        }

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (int i = 0; i < def.verticesCount; i++) {
            int x = def.verticesX[i];
            int y = def.verticesY[i];
            int z = def.verticesZ[i];
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
            if (z < minZ) minZ = z;
            if (z > maxZ) maxZ = z;
        }
        long spanX = Math.abs((long) maxX - minX);
        long spanY = Math.abs((long) maxY - minY);
        long spanZ = Math.abs((long) maxZ - minZ);
        return spanX + spanY + spanZ >= 10;
    }

    private static boolean validateModelInvariants(Model def, int modelId, boolean expectsFaceTypes, boolean expectsPriorities) {
        boolean ok = true;
        StringBuilder issues = DEBUG_MODEL_VALIDATION ? new StringBuilder() : null;

        if (def.texturesCount > 0) {
            if (def.texturesX == null || def.texturesY == null || def.texturesZ == null) {
                ok = false;
                if (issues != null) issues.append("textureXYZ null;");
            } else if (def.texturesX.length < def.texturesCount
                    || def.texturesY.length < def.texturesCount
                    || def.texturesZ.length < def.texturesCount) {
                ok = false;
                if (issues != null) issues.append("textureXYZ length;");
            }
        }

        if (def.textures != null && def.textures.length < def.trianglesCount) {
            ok = false;
            if (issues != null) issues.append("textures length;");
        }

        if (def.materials != null && def.materials.length < def.trianglesCount) {
            ok = false;
            if (issues != null) issues.append("materials length;");
        }

        if (expectsFaceTypes) {
            if (def.types == null || def.types.length < def.trianglesCount) {
                ok = false;
                if (issues != null) issues.append("types;");
            }
        } else if (def.types != null && def.types.length < def.trianglesCount) {
            ok = false;
            if (issues != null) issues.append("types length;");
        }

        if (expectsPriorities) {
            if (def.face_render_priorities == null || def.face_render_priorities.length < def.trianglesCount) {
                ok = false;
                if (issues != null) issues.append("priorities;");
            }
        } else if (def.face_render_priorities != null && def.face_render_priorities.length < def.trianglesCount) {
            ok = false;
            if (issues != null) issues.append("priorities length;");
        }

        if (!ok && DEBUG_MODEL_VALIDATION) {
            System.out.println("[model validate] id=" + modelId + " issues=" + issues);
        }
        return ok;
    }

    private static void applyTextureCompatibilityFallback(Model def, int modelId, String reason) {
        if (def.texturesCount == 0 && def.textures == null && def.texturesX == null && def.materials == null) {
            return;
        }
        def.texturesCount = 0;
        def.texturesX = null;
        def.texturesY = null;
        def.texturesZ = null;
        def.textureTypes = null;
        def.textures = null;
        def.materials = null;
        if (DEBUG_MODEL_VALIDATION) {
            System.out.println("[model validate] id=" + modelId + " textures stripped (" + reason + ")");
        }
    }




    public static void decodeType3(Model def, byte[] var1)
    {
        Buffer var2 = new Buffer(var1);
        Buffer var3 = new Buffer(var1);
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var2.setOffset(var1.length - 26);
        int var9 = var2.readUShort();
        int var10 = var2.readUShort();
        int var11 = var2.readUnsignedByte();
        int var12 = var2.readUnsignedByte();
        int var13 = var2.readUnsignedByte();
        int var14 = var2.readUnsignedByte();
        int var15 = var2.readUnsignedByte();
        int var16 = var2.readUnsignedByte();
        int var17 = var2.readUnsignedByte();
        int var18 = var2.readUnsignedByte();
        int var19 = var2.readUShort();
        int var20 = var2.readUShort();
        int var21 = var2.readUShort();
        int var22 = var2.readUShort();
        int var23 = var2.readUShort();
        int var24 = var2.readUShort();
        int var25 = 0;
        int var26 = 0;
        int var27 = 0;
        int var28;


        if (var11 > 0)
        {
            def.textureTypes = new byte[var11];
            var2.setOffset(0);

            for (var28 = 0; var28 < var11; ++var28)
            {
                byte var29 = def.textureTypes[var28] = var2.readByte();
                if (var29 == 0)
                {
                    ++var25;
                }

                if (var29 >= 1 && var29 <= 3)
                {
                    ++var26;
                }

                if (var29 == 2)
                {
                    ++var27;
                }
            }
        }

        var28 = var11 + var9;
        int var58 = var28;
        if (var12 == 1)
        {
            var28 += var10;
        }

        int var30 = var28;
        var28 += var10;
        int var31 = var28;
        if (var13 == 255)
        {
            var28 += var10;
        }

        int var32 = var28;
        if (var15 == 1)
        {
            var28 += var10;
        }

        int var33 = var28;
        var28 += var24;
        int var34 = var28;
        if (var14 == 1)
        {
            var28 += var10;
        }

        int var35 = var28;
        var28 += var22;
        int var36 = var28;
        if (var16 == 1)
        {
            var28 += var10 * 2;
        }

        int var37 = var28;
        var28 += var23;
        int var38 = var28;
        var28 += var10 * 2;
        int var39 = var28;
        var28 += var19;
        int var40 = var28;
        var28 += var20;
        int var41 = var28;
        var28 += var21;
        int var42 = var28;
        var28 += var25 * 6;
        int var43 = var28;
        var28 += var26 * 6;
        int var44 = var28;
        var28 += var26 * 6;
        int var45 = var28;
        var28 += var26 * 2;
        int var46 = var28;
        var28 += var26;
        int var47 = var28;
        var28 = var28 + var26 * 2 + var27 * 2;


        def.verticesCount = var9;
        def.trianglesCount = var10;
        def.texturesCount = var11;
        def.verticesX = new int[var9];
        def.verticesY = new int[var9];
        def.verticesZ = new int[var9];
        def.trianglesX = new int[var10];
        def.trianglesY = new int[var10];
        def.trianglesZ = new int[var10];
        if (var17 == 1)
        {
            def.vertexData = new int[var9];
        }

        if (var12 == 1)
        {
            def.types = new int[var10];
        }

        if (var13 == 255)
        {
            def.face_render_priorities = new byte[var10];
        }
        else
        {
            def.face_priority = (byte) var13;
        }

        if (var14 == 1)
        {
            def.alphas = new int[var10];
        }

        if (var15 == 1)
        {
            def.triangleData = new int[var10];
        }

        if (var16 == 1)
        {
            def.materials = new short[var10];
        }

        if (var16 == 1 && var11 > 0)
        {
            def.textures = new byte[var10];
        }

        if (var18 == 1)
        {
            def.animayaGroups = new int[var9][];
            def.animayaScales = new int[var9][];
        }

        def.colors = new short[var10];
        if (var11 > 0)
        {
            def.texturesX = new short[var11];
            def.texturesY = new short[var11];
            def.texturesZ = new short[var11];
        }

        var2.setOffset(var11);
        var3.setOffset(var39);
        var4.setOffset(var40);
        var5.setOffset(var41);
        var6.setOffset(var33);
        int var48 = 0;
        int var49 = 0;
        int var50 = 0;

        int var51;
        int var52;
        int var53;
        int var54;
        int var55;
        for (var51 = 0; var51 < var9; ++var51)
        {
            var52 = var2.readUnsignedByte();
            var53 = 0;
            if ((var52 & 1) != 0)
            {
                var53 = var3.readSmart();
            }

            var54 = 0;
            if ((var52 & 2) != 0)
            {
                var54 = var4.readSmart();
            }

            var55 = 0;
            if ((var52 & 4) != 0)
            {
                var55 = var5.readSmart();
            }

            def.verticesX[var51] = var48 + var53;
            def.verticesY[var51] = var49 + var54;
            def.verticesZ[var51] = var50 + var55;
            var48 = def.verticesX[var51];
            var49 = def.verticesY[var51];
            var50 = def.verticesZ[var51];
            if (var17 == 1)
            {
                def.vertexData[var51] = var6.readUnsignedByte();
            }
        }

        if (var18 == 1)
        {
            for (var51 = 0; var51 < var9; ++var51)
            {
                var52 = var6.readUnsignedByte();
                def.animayaGroups[var51] = new int[var52];
                def.animayaScales[var51] = new int[var52];

                for (var53 = 0; var53 < var52; ++var53)
                {
                    def.animayaGroups[var51][var53] = var6.readUnsignedByte();
                    def.animayaScales[var51][var53] = var6.readUnsignedByte();
                }
            }
        }

        var2.setOffset(var38);
        var3.setOffset(var58);
        var4.setOffset(var31);
        var5.setOffset(var34);
        var6.setOffset(var32);
        var7.setOffset(var36);
        var8.setOffset(var37);

        for (var51 = 0; var51 < var10; ++var51)
        {
            def.colors[var51] = (short) var2.readUShort();
            if (var12 == 1)
            {
                def.types[var51] = var3.readByte();
            }

            if (var13 == 255)
            {
                def.face_render_priorities[var51] = var4.readByte();
            }

            if (var14 == 1)
            {
                def.alphas[var51] = var5.readByte();
                if (def.alphas[var51] < 0) {
                    def.alphas[var51] = (256 + def.alphas[var51]);
                }
            }

            if (var15 == 1)
            {
                def.triangleData[var51] = var6.readUnsignedByte();
            }

            if (var16 == 1)
            {
                def.materials[var51] = (short) (var7.readUShort() - 1);
            }

            if (def.textures != null && def.materials[var51] != -1)
            {
                def.textures[var51] = (byte) (var8.readUnsignedByte() - 1);
            }
        }

        var2.setOffset(var35);
        var3.setOffset(var30);
        var51 = 0;
        var52 = 0;
        var53 = 0;
        var54 = 0;

        int var56;
        for (var55 = 0; var55 < var10; ++var55)
        {
            var56 = var3.readUnsignedByte();
            if (var56 == 1)
            {
                var51 = var2.readSmart() + var54;
                var52 = var2.readSmart() + var51;
                var53 = var2.readSmart() + var52;
                var54 = var53;
                def.trianglesX[var55] = var51;
                def.trianglesY[var55] = var52;
                def.trianglesZ[var55] = var53;
            }

            if (var56 == 2)
            {
                var52 = var53;
                var53 = var2.readSmart() + var54;
                var54 = var53;
                def.trianglesX[var55] = var51;
                def.trianglesY[var55] = var52;
                def.trianglesZ[var55] = var53;
            }

            if (var56 == 3)
            {
                var51 = var53;
                var53 = var2.readSmart() + var54;
                var54 = var53;
                def.trianglesX[var55] = var51;
                def.trianglesY[var55] = var52;
                def.trianglesZ[var55] = var53;
            }

            if (var56 == 4)
            {
                int var57 = var51;
                var51 = var52;
                var52 = var57;
                var53 = var2.readSmart() + var54;
                var54 = var53;
                def.trianglesX[var55] = var51;
                def.trianglesY[var55] = var57;
                def.trianglesZ[var55] = var53;
            }
        }

        var2.setOffset(var42);
        var3.setOffset(var43);
        var4.setOffset(var44);
        var5.setOffset(var45);
        var6.setOffset(var46);
        var7.setOffset(var47);

        for (var55 = 0; var55 < var11; ++var55)
        {
            var56 = def.textureTypes[var55] & 255;
            if (var56 == 0)
            {
                def.texturesX[var55] = (short) var2.readUShort();
                def.texturesY[var55] = (short) var2.readUShort();
                def.texturesZ[var55] = (short) var2.readUShort();
            }
        }

        var2.setOffset(var28);
        var55 = var2.readUnsignedByte();
        if (var55 != 0)
        {
            var2.readUShort();
            var2.readUShort();
            var2.readUShort();
            var2.readInt();
        }

    }


    public static void decodeType2(Model def, byte[] var1)
    {
        boolean var2 = false;
        boolean var3 = false;
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var4.setOffset(var1.length - 23);
        int var9 = var4.readUShort();
        int var10 = var4.readUShort();
        int var11 = var4.readUnsignedByte();
        int var12 = var4.readUnsignedByte();
        int var13 = var4.readUnsignedByte();
        int var14 = var4.readUnsignedByte();
        int var15 = var4.readUnsignedByte();
        int var16 = var4.readUnsignedByte();
        int var17 = var4.readUnsignedByte();
        int var18 = var4.readUShort();
        int var19 = var4.readUShort();
        int var20 = var4.readUShort();
        int var21 = var4.readUShort();
        int var22 = var4.readUShort();
        byte var23 = 0;
        int var24 = var23 + var9;
        int var25 = var24;
        var24 += var10;
        int var26 = var24;
        if (var13 == 255)
        {
            var24 += var10;
        }

        int var27 = var24;
        if (var15 == 1)
        {
            var24 += var10;
        }

        int var28 = var24;
        if (var12 == 1)
        {
            var24 += var10;
        }

        int var29 = var24;
        var24 += var22;
        int var30 = var24;
        if (var14 == 1)
        {
            var24 += var10;
        }

        int var31 = var24;
        var24 += var21;
        int var32 = var24;
        var24 += var10 * 2;
        int var33 = var24;
        var24 += var11 * 6;
        int var34 = var24;
        var24 += var18;
        int var35 = var24;
        var24 += var19;
        int var10000 = var24 + var20;
        def.verticesCount = var9;
        def.trianglesCount = var10;
        def.texturesCount = var11;
        def.verticesX = new int[var9];
        def.verticesY = new int[var9];
        def.verticesZ = new int[var9];
        def.trianglesX = new int[var10];
        def.trianglesY = new int[var10];
        def.trianglesZ = new int[var10];
        if (var11 > 0)
        {
            def.textureTypes = new byte[var11];
            def.texturesX = new short[var11];
            def.texturesY = new short[var11];
            def.texturesZ = new short[var11];
        }

        if (var16 == 1)
        {
            def.vertexData = new int[var9];
        }

        if (var12 == 1)
        {
            def.types = new int[var10];
            def.textures = new byte[var10];
            def.materials = new short[var10];
        }

        if (var13 == 255)
        {
            def.face_render_priorities = new byte[var10];
        }
        else
        {
            def.face_priority = (byte) var13;
        }

        if (var14 == 1)
        {
            def.alphas = new int[var10];
        }

        if (var15 == 1)
        {
            def.triangleData = new int[var10];
        }

        if (var17 == 1)
        {
            def.animayaGroups = new int[var9][];
            def.animayaScales = new int[var9][];
        }

        def.colors = new short[var10];
        var4.setOffset(var23);
        var5.setOffset(var34);
        var6.setOffset(var35);
        var7.setOffset(var24);
        var8.setOffset(var29);
        int var37 = 0;
        int var38 = 0;
        int var39 = 0;

        int var40;
        int var41;
        int var42;
        int var43;
        int var44;
        for (var40 = 0; var40 < var9; ++var40)
        {
            var41 = var4.readUnsignedByte();
            var42 = 0;
            if ((var41 & 1) != 0)
            {
                var42 = var5.readSmart();
            }

            var43 = 0;
            if ((var41 & 2) != 0)
            {
                var43 = var6.readSmart();
            }

            var44 = 0;
            if ((var41 & 4) != 0)
            {
                var44 = var7.readSmart();
            }

            def.verticesX[var40] = var37 + var42;
            def.verticesY[var40] = var38 + var43;
            def.verticesZ[var40] = var39 + var44;
            var37 = def.verticesX[var40];
            var38 = def.verticesY[var40];
            var39 = def.verticesZ[var40];
            if (var16 == 1)
            {
                def.vertexData[var40] = var8.readUnsignedByte();
            }
        }

        if (var17 == 1)
        {
            for (var40 = 0; var40 < var9; ++var40)
            {
                var41 = var8.readUnsignedByte();
                def.animayaGroups[var40] = new int[var41];
                def.animayaScales[var40] = new int[var41];

                for (var42 = 0; var42 < var41; ++var42)
                {
                    def.animayaGroups[var40][var42] = var8.readUnsignedByte();
                    def.animayaScales[var40][var42] = var8.readUnsignedByte();
                }
            }
        }

        var4.setOffset(var32);
        var5.setOffset(var28);
        var6.setOffset(var26);
        var7.setOffset(var30);
        var8.setOffset(var27);

        for (var40 = 0; var40 < var10; ++var40)
        {
            def.colors[var40] = (short) var4.readUShort();
            if (var12 == 1)
            {
                var41 = var5.readUnsignedByte();
                if ((var41 & 1) == 1)
                {
                    def.types[var40] = 1;
                    var2 = true;
                }
                else
                {
                    def.types[var40] = 0;
                }

                if ((var41 & 2) == 2)
                {
                    def.textures[var40] = (byte) (var41 >> 2);
                    def.materials[var40] = def.colors[var40];
                    def.colors[var40] = 127;
                    if (def.materials[var40] != -1)
                    {
                        var3 = true;
                    }
                }
                else
                {
                    def.textures[var40] = -1;
                    def.materials[var40] = -1;
                }
            }

            if (var13 == 255)
            {
                def.face_render_priorities[var40] = var6.readByte();
            }

            if (var14 == 1)
            {
                def.alphas[var40] = var7.readByte();
                if (def.alphas[var40] < 0) {
                    def.alphas[var40] = (256 + def.alphas[var40]);
                }
            }

            if (var15 == 1)
            {
                def.triangleData[var40] = var8.readUnsignedByte();
            }
        }

        var4.setOffset(var31);
        var5.setOffset(var25);
        var40 = 0;
        var41 = 0;
        var42 = 0;
        var43 = 0;

        int var45;
        int var46;
        for (var44 = 0; var44 < var10; ++var44)
        {
            var45 = var5.readUnsignedByte();
            if (var45 == 1)
            {
                var40 = var4.readSmart() + var43;
                var41 = var4.readSmart() + var40;
                var42 = var4.readSmart() + var41;
                var43 = var42;
                def.trianglesX[var44] = var40;
                def.trianglesY[var44] = var41;
                def.trianglesZ[var44] = var42;
            }

            if (var45 == 2)
            {
                var41 = var42;
                var42 = var4.readSmart() + var43;
                var43 = var42;
                def.trianglesX[var44] = var40;
                def.trianglesY[var44] = var41;
                def.trianglesZ[var44] = var42;
            }

            if (var45 == 3)
            {
                var40 = var42;
                var42 = var4.readSmart() + var43;
                var43 = var42;
                def.trianglesX[var44] = var40;
                def.trianglesY[var44] = var41;
                def.trianglesZ[var44] = var42;
            }

            if (var45 == 4)
            {
                var46 = var40;
                var40 = var41;
                var41 = var46;
                var42 = var4.readSmart() + var43;
                var43 = var42;
                def.trianglesX[var44] = var40;
                def.trianglesY[var44] = var46;
                def.trianglesZ[var44] = var42;
            }
        }

        var4.setOffset(var33);

        for (var44 = 0; var44 < var11; ++var44)
        {
            def.textureTypes[var44] = 0;
            def.texturesX[var44] = (short) var4.readUShort();
            def.texturesY[var44] = (short) var4.readUShort();
            def.texturesZ[var44] = (short) var4.readUShort();
        }

        if (def.textures != null)
        {
            boolean var47 = false;

            for (var45 = 0; var45 < var10; ++var45)
            {
                var46 = def.textures[var45] & 255;
                if (var46 != 255)
                {
                    if (def.trianglesX[var45] == (def.texturesX[var46] & '\uffff') && def.trianglesY[var45] == (def.texturesY[var46] & '\uffff') && def.trianglesZ[var45] == (def.texturesZ[var46] & '\uffff'))
                    {
                        def.textures[var45] = -1;
                    }
                    else
                    {
                        var47 = true;
                    }
                }
            }

            if (!var47)
            {
                def.textures = null;
            }
        }

        if (!var3)
        {
            def.materials = null;
        }

        if (!var2)
        {
            def.types = null;
        }

    }
    private static final Decoder TYPE525 = new Decoder() {
        @Override
        public String name() { return "type525"; }

        @Override
        public void decode(Model def, byte[] data) {
            ModelLoader.decodeType525(def, data);
        }
    };
    public static void decodeType1(Model def, byte[] var1)
    {
        Buffer var2 = new Buffer(var1);
        Buffer var3 = new Buffer(var1);
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var2.setOffset(var1.length - 23);
        int var9 = var2.readUShort();
        int var10 = var2.readUShort();
        int var11 = var2.readUnsignedByte();
        int var12 = var2.readUnsignedByte();
        int var13 = var2.readUnsignedByte();
        int var14 = var2.readUnsignedByte();
        int var15 = var2.readUnsignedByte();
        int var16 = var2.readUnsignedByte();
        int var17 = var2.readUnsignedByte();
        int var18 = var2.readUShort();
        int var19 = var2.readUShort();
        int var20 = var2.readUShort();
        int var21 = var2.readUShort();
        int var22 = var2.readUShort();
        int var23 = 0;
        int var24 = 0;
        int var25 = 0;
        int var26;
        if (var11 > 0)
        {
            def.textureTypes = new byte[var11];
            var2.setOffset(0);

            for (var26 = 0; var26 < var11; ++var26)
            {
                byte var27 = def.textureTypes[var26] = var2.readByte();
                if (var27 == 0)
                {
                    ++var23;
                }

                if (var27 >= 1 && var27 <= 3)
                {
                    ++var24;
                }

                if (var27 == 2)
                {
                    ++var25;
                }
            }
        }

        var26 = var11 + var9;
        int var56 = var26;
        if (var12 == 1)
        {
            var26 += var10;
        }

        int var28 = var26;
        var26 += var10;
        int var29 = var26;
        if (var13 == 255)
        {
            var26 += var10;
        }

        int var30 = var26;
        if (var15 == 1)
        {
            var26 += var10;
        }

        int var31 = var26;
        if (var17 == 1)
        {
            var26 += var9;
        }

        int var32 = var26;
        if (var14 == 1)
        {
            var26 += var10;
        }

        int var33 = var26;
        var26 += var21;
        int var34 = var26;
        if (var16 == 1)
        {
            var26 += var10 * 2;
        }

        int var35 = var26;
        var26 += var22;
        int var36 = var26;
        var26 += var10 * 2;
        int var37 = var26;
        var26 += var18;
        int var38 = var26;
        var26 += var19;
        int var39 = var26;
        var26 += var20;
        int var40 = var26;
        var26 += var23 * 6;
        int var41 = var26;
        var26 += var24 * 6;
        int var42 = var26;
        var26 += var24 * 6;
        int var43 = var26;
        var26 += var24 * 2;
        int var44 = var26;
        var26 += var24;
        int var45 = var26;
        var26 = var26 + var24 * 2 + var25 * 2;
        def.verticesCount = var9;
        def.trianglesCount = var10;
        def.texturesCount = var11;
        def.verticesX = new int[var9];
        def.verticesY = new int[var9];
        def.verticesZ = new int[var9];
        def.trianglesX = new int[var10];
        def.trianglesY = new int[var10];
        def.trianglesZ = new int[var10];
        if (var17 == 1)
        {
            def.vertexData = new int[var9];
        }

        if (var12 == 1)
        {
            def.types = new int[var10];
        }

        if (var13 == 255)
        {
            def.face_render_priorities = new byte[var10];
        }
        else
        {
            def.face_priority = (byte) var13;
        }

        if (var14 == 1)
        {
            def.alphas = new int[var10];
        }

        if (var15 == 1)
        {
            def.triangleData = new int[var10];
        }

        if (var16 == 1)
        {
            def.materials = new short[var10];
        }

        if (var16 == 1 && var11 > 0)
        {
            def.textures = new byte[var10];
        }

        def.colors = new short[var10];
        if (var11 > 0)
        {
            def.texturesX = new short[var11];
            def.texturesY = new short[var11];
            def.texturesZ = new short[var11];
        }

        var2.setOffset(var11);
        var3.setOffset(var37);
        var4.setOffset(var38);
        var5.setOffset(var39);
        var6.setOffset(var31);
        int var46 = 0;
        int var47 = 0;
        int var48 = 0;

        int var49;
        int var50;
        int var51;
        int var52;
        int var53;
        for (var49 = 0; var49 < var9; ++var49)
        {
            var50 = var2.readUnsignedByte();
            var51 = 0;
            if ((var50 & 1) != 0)
            {
                var51 = var3.readSmart();
            }

            var52 = 0;
            if ((var50 & 2) != 0)
            {
                var52 = var4.readSmart();
            }

            var53 = 0;
            if ((var50 & 4) != 0)
            {
                var53 = var5.readSmart();
            }

            def.verticesX[var49] = var46 + var51;
            def.verticesY[var49] = var47 + var52;
            def.verticesZ[var49] = var48 + var53;
            var46 = def.verticesX[var49];
            var47 = def.verticesY[var49];
            var48 = def.verticesZ[var49];
            if (var17 == 1)
            {
                def.vertexData[var49] = var6.readUnsignedByte();
            }
        }

        var2.setOffset(var36);
        var3.setOffset(var56);
        var4.setOffset(var29);
        var5.setOffset(var32);
        var6.setOffset(var30);
        var7.setOffset(var34);
        var8.setOffset(var35);

        for (var49 = 0; var49 < var10; ++var49)
        {
            def.colors[var49] = (short) var2.readUShort();
            if (var12 == 1)
            {
                def.types[var49] = var3.readByte();
            }

            if (var13 == 255)
            {
                def.face_render_priorities[var49] = var4.readByte();
            }

            if (var14 == 1)
            {
                def.alphas[var49] = var5.readByte();
                if (def.alphas[var49] < 0) {
                    def.alphas[var49] = (256 + def.alphas[var40]);
                }
            }

            if (var15 == 1)
            {
                def.triangleData[var49] = var6.readUnsignedByte();
            }

            if (var16 == 1)
            {
                def.materials[var49] = (short) (var7.readUShort() - 1);
            }

            if (def.textures != null && def.materials[var49] != -1)
            {
                def.textures[var49] = (byte) (var8.readUnsignedByte() - 1);
            }
        }

        var2.setOffset(var33);
        var3.setOffset(var28);
        var49 = 0;
        var50 = 0;
        var51 = 0;
        var52 = 0;

        int var54;
        for (var53 = 0; var53 < var10; ++var53)
        {
            var54 = var3.readUnsignedByte();
            if (var54 == 1)
            {
                var49 = var2.readSmart() + var52;
                var50 = var2.readSmart() + var49;
                var51 = var2.readSmart() + var50;
                var52 = var51;
                def.trianglesX[var53] = var49;
                def.trianglesY[var53] = var50;
                def.trianglesZ[var53] = var51;
            }

            if (var54 == 2)
            {
                var50 = var51;
                var51 = var2.readSmart() + var52;
                var52 = var51;
                def.trianglesX[var53] = var49;
                def.trianglesY[var53] = var50;
                def.trianglesZ[var53] = var51;
            }

            if (var54 == 3)
            {
                var49 = var51;
                var51 = var2.readSmart() + var52;
                var52 = var51;
                def.trianglesX[var53] = var49;
                def.trianglesY[var53] = var50;
                def.trianglesZ[var53] = var51;
            }

            if (var54 == 4)
            {
                int var55 = var49;
                var49 = var50;
                var50 = var55;
                var51 = var2.readSmart() + var52;
                var52 = var51;
                def.trianglesX[var53] = var49;
                def.trianglesY[var53] = var55;
                def.trianglesZ[var53] = var51;
            }
        }

        var2.setOffset(var40);
        var3.setOffset(var41);
        var4.setOffset(var42);
        var5.setOffset(var43);
        var6.setOffset(var44);
        var7.setOffset(var45);

        for (var53 = 0; var53 < var11; ++var53)
        {
            var54 = def.textureTypes[var53] & 255;
            if (var54 == 0)
            {
                def.texturesX[var53] = (short) var2.readUShort();
                def.texturesY[var53] = (short) var2.readUShort();
                def.texturesZ[var53] = (short) var2.readUShort();
            }
        }

        var2.setOffset(var26);
        var53 = var2.readUnsignedByte();
        if (var53 != 0)
        {
            var2.readUShort();
            var2.readUShort();
            var2.readUShort();
            var2.readInt();
        }

    }

    public static void decodeOldFormat(Model def, byte[] var1)
    {
        boolean var2 = false;
        boolean var3 = false;
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var4.setOffset(var1.length - 18);
        int var9 = var4.readUShort();
        int var10 = var4.readUShort();
        int var11 = var4.readUnsignedByte();
        int var12 = var4.readUnsignedByte();
        int var13 = var4.readUnsignedByte();
        int var14 = var4.readUnsignedByte();
        int var15 = var4.readUnsignedByte();
        int var16 = var4.readUnsignedByte();
        int var17 = var4.readUShort();
        int var18 = var4.readUShort();
        int var19 = var4.readUShort();
        int var20 = var4.readUShort();
        byte var21 = 0;
        int var22 = var21 + var9;
        int var23 = var22;
        var22 += var10;
        int var24 = var22;
        if (var13 == 255)
        {
            var22 += var10;
        }

        int var25 = var22;
        if (var15 == 1)
        {
            var22 += var10;
        }

        int var26 = var22;
        if (var12 == 1)
        {
            var22 += var10;
        }

        int var27 = var22;
        if (var16 == 1)
        {
            var22 += var9;
        }

        int var28 = var22;
        if (var14 == 1)
        {
            var22 += var10;
        }

        int var29 = var22;
        var22 += var20;
        int var30 = var22;
        var22 += var10 * 2;
        int var31 = var22;
        var22 += var11 * 6;
        int var32 = var22;
        var22 += var17;
        int var33 = var22;
        var22 += var18;
        int var10000 = var22 + var19;
        def.verticesCount = var9;
        def.trianglesCount = var10;
        def.texturesCount = var11;
        def.verticesX = new int[var9];
        def.verticesY = new int[var9];
        def.verticesZ = new int[var9];
        def.trianglesX = new int[var10];
        def.trianglesY = new int[var10];
        def.trianglesZ = new int[var10];
        if (var11 > 0)
        {
            def.textureTypes = new byte[var11];
            def.texturesX = new short[var11];
            def.texturesY = new short[var11];
            def.texturesZ = new short[var11];
        }

        if (var16 == 1)
        {
            def.vertexData = new int[var9];
        }

        if (var12 == 1)
        {
            def.types = new int[var10];
            def.textures = new byte[var10];
            def.materials = new short[var10];
        }

        if (var13 == 255)
        {
            def.face_render_priorities = new byte[var10];
        }
        else
        {
            def.face_priority = (byte) var13;
        }

        if (var14 == 1)
        {
            def.alphas = new int[var10];
        }

        if (var15 == 1)
        {
            def.triangleData = new int[var10];
        }

        def.colors = new short[var10];
        var4.setOffset(var21);
        var5.setOffset(var32);
        var6.setOffset(var33);
        var7.setOffset(var22);
        var8.setOffset(var27);
        int var35 = 0;
        int var36 = 0;
        int var37 = 0;

        int var38;
        int var39;
        int var40;
        int var41;
        int var42;
        for (var38 = 0; var38 < var9; ++var38)
        {
            var39 = var4.readUnsignedByte();
            var40 = 0;
            if ((var39 & 1) != 0)
            {
                var40 = var5.readSmart();
            }

            var41 = 0;
            if ((var39 & 2) != 0)
            {
                var41 = var6.readSmart();
            }

            var42 = 0;
            if ((var39 & 4) != 0)
            {
                var42 = var7.readSmart();
            }

            def.verticesX[var38] = var35 + var40;
            def.verticesY[var38] = var36 + var41;
            def.verticesZ[var38] = var37 + var42;
            var35 = def.verticesX[var38];
            var36 = def.verticesY[var38];
            var37 = def.verticesZ[var38];
            if (var16 == 1)
            {
                def.vertexData[var38] = var8.readUnsignedByte();
            }
        }

        var4.setOffset(var30);
        var5.setOffset(var26);
        var6.setOffset(var24);
        var7.setOffset(var28);
        var8.setOffset(var25);

        for (var38 = 0; var38 < var10; ++var38)
        {
            def.colors[var38] = (short) var4.readUShort();
            if (var12 == 1)
            {
                var39 = var5.readUnsignedByte();
                if ((var39 & 1) == 1)
                {
                    def.types[var38] = 1;
                    var2 = true;
                }
                else
                {
                    def.types[var38] = 0;
                }

                if ((var39 & 2) == 2)
                {
                    def.textures[var38] = (byte) (var39 >> 2);
                    def.materials[var38] = def.colors[var38];
                    def.colors[var38] = 127;
                    if (def.materials[var38] != -1)
                    {
                        var3 = true;
                    }
                }
                else
                {
                    def.textures[var38] = -1;
                    def.materials[var38] = -1;
                }
            }

            if (var13 == 255)
            {
                def.face_render_priorities[var38] = var6.readByte();
            }

            if (var14 == 1)
            {
                def.alphas[var38] = var7.readByte();
                if (def.alphas[var38] < 0) {
                    def.alphas[var38] = (256 + def.alphas[var38]);
                }
            }

            if (var15 == 1)
            {
                def.triangleData[var38] = var8.readUnsignedByte();
            }
        }

        var4.setOffset(var29);
        var5.setOffset(var23);
        var38 = 0;
        var39 = 0;
        var40 = 0;
        var41 = 0;

        int var43;
        int var44;
        for (var42 = 0; var42 < var10; ++var42)
        {
            var43 = var5.readUnsignedByte();
            if (var43 == 1)
            {
                var38 = var4.readSmart() + var41;
                var39 = var4.readSmart() + var38;
                var40 = var4.readSmart() + var39;
                var41 = var40;
                def.trianglesX[var42] = var38;
                def.trianglesY[var42] = var39;
                def.trianglesZ[var42] = var40;
            }

            if (var43 == 2)
            {
                var39 = var40;
                var40 = var4.readSmart() + var41;
                var41 = var40;
                def.trianglesX[var42] = var38;
                def.trianglesY[var42] = var39;
                def.trianglesZ[var42] = var40;
            }

            if (var43 == 3)
            {
                var38 = var40;
                var40 = var4.readSmart() + var41;
                var41 = var40;
                def.trianglesX[var42] = var38;
                def.trianglesY[var42] = var39;
                def.trianglesZ[var42] = var40;
            }

            if (var43 == 4)
            {
                var44 = var38;
                var38 = var39;
                var39 = var44;
                var40 = var4.readSmart() + var41;
                var41 = var40;
                def.trianglesX[var42] = var38;
                def.trianglesY[var42] = var44;
                def.trianglesZ[var42] = var40;
            }
        }

        var4.setOffset(var31);

        for (var42 = 0; var42 < var11; ++var42)
        {
            def.textureTypes[var42] = 0;
            def.texturesX[var42] = (short) var4.readUShort();
            def.texturesY[var42] = (short) var4.readUShort();
            def.texturesZ[var42] = (short) var4.readUShort();
        }

        if (def.textures != null)
        {
            boolean var45 = false;

            for (var43 = 0; var43 < var10; ++var43)
            {
                var44 = def.textures[var43] & 255;
                if (var44 != 255)
                {
                    if (def.trianglesX[var43] == (def.texturesX[var44] & '\uffff') && def.trianglesY[var43] == (def.texturesY[var44] & '\uffff') && def.trianglesZ[var43] == (def.texturesZ[var44] & '\uffff'))
                    {
                        def.textures[var43] = -1;
                    }
                    else
                    {
                        var45 = true;
                    }
                }
            }

            if (!var45)
            {
                def.textures = null;
            }
        }

        if (!var3)
        {
            def.materials = null;
        }

        if (!var2)
        {
            def.types = null;
        }

    }
}

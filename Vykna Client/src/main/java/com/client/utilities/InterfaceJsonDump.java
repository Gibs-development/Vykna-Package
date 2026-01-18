package com.client.utilities;

import com.client.graphics.interfaces.RSInterface;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class InterfaceJsonDump {

    public static void dumpInterfaceTree(int rootId, String outPath) {
        RSInterface root = RSInterface.interfaceCache[rootId];
        if (root == null) {
            System.out.println("dumpInterfaceTree: interface " + rootId + " is null");
            return;
        }

        StringBuilder sb = new StringBuilder(256 * 1024);
        Set<Integer> visited = new HashSet<>();
        writeInterfaceRecursive(sb, rootId, visited, 0);

        try (FileWriter fw = new FileWriter(outPath)) {
            fw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Wrote interface JSON dump to: " + outPath);
    }

    private static void writeInterfaceRecursive(StringBuilder sb, int id, Set<Integer> visited, int depth) {
        if (!visited.add(id)) return;

        RSInterface r = RSInterface.interfaceCache[id];
        if (r == null) return;

        indent(sb, depth).append("{\n");
        indent(sb, depth + 1).append("\"id\": ").append(id).append(",\n");

        // Core fields that exist in your RSInterface
        num(sb, depth + 1, "parentID", r.parentID);
        num(sb, depth + 1, "type", r.type);
        num(sb, depth + 1, "atActionType", r.atActionType);
        num(sb, depth + 1, "contentType", r.contentType);
        num(sb, depth + 1, "width", r.width);
        num(sb, depth + 1, "height", r.height);
        num(sb, depth + 1, "scrollMax", r.scrollMax);
        num(sb, depth + 1, "transparency", r.transparency);
        num(sb, depth + 1, "hoverType", r.hoverType);
        num(sb, depth + 1, "mOverInterToTrigger", r.mOverInterToTrigger);

        bool(sb, depth + 1, "isMouseoverTriggered", r.isMouseoverTriggered);
        bool(sb, depth + 1, "drawsTransparent", r.drawsTransparent);
        bool(sb, depth + 1, "centerText", r.centerText);
        bool(sb, depth + 1, "textShadow", r.textShadow);

        str(sb, depth + 1, "message", r.message);
        str(sb, depth + 1, "tooltip", r.tooltip);

        // Sprite info (Codex can use this to match “which sprite is which”)
        if (r.sprite1 != null) {
            indent(sb, depth + 1).append("\"sprite1\": {\"w\": ")
                    .append(r.sprite1.myWidth).append(", \"h\": ")
                    .append(r.sprite1.myHeight).append("},\n");
        } else {
            indent(sb, depth + 1).append("\"sprite1\": null,\n");
        }

        if (r.sprite2 != null) {
            indent(sb, depth + 1).append("\"sprite2\": {\"w\": ")
                    .append(r.sprite2.myWidth).append(", \"h\": ")
                    .append(r.sprite2.myHeight).append("},\n");
        } else {
            indent(sb, depth + 1).append("\"sprite2\": null,\n");
        }

        // Children list with coordinates stored on the parent
        int total = (r.children == null) ? 0 : r.children.length;
        indent(sb, depth + 1).append("\"childrenCount\": ").append(total).append(",\n");
        indent(sb, depth + 1).append("\"children\": [\n");

        if (total > 0) {
            for (int i = 0; i < total; i++) {
                int childId = r.children[i];
                int cx = (r.childX != null && i < r.childX.length) ? r.childX[i] : 0;
                int cy = (r.childY != null && i < r.childY.length) ? r.childY[i] : 0;

                RSInterface c = (childId > 0 && childId < RSInterface.interfaceCache.length)
                        ? RSInterface.interfaceCache[childId] : null;

                indent(sb, depth + 2).append("{");
                sb.append("\"index\": ").append(i)
                        .append(", \"childId\": ").append(childId)
                        .append(", \"x\": ").append(cx)
                        .append(", \"y\": ").append(cy);

                // add a tiny bit of metadata about the child to help identification
                if (c != null) {
                    sb.append(", \"childType\": ").append(c.type)
                            .append(", \"childW\": ").append(c.width)
                            .append(", \"childH\": ").append(c.height);
                    if (c.message != null && !c.message.isEmpty()) {
                        sb.append(", \"childMessage\": ").append(jsonString(c.message));
                    }
                    if (c.tooltip != null && !c.tooltip.isEmpty()) {
                        sb.append(", \"childTooltip\": ").append(jsonString(c.tooltip));
                    }
                    if (c.sprite1 != null) {
                        sb.append(", \"childSprite1\": {\"w\": ").append(c.sprite1.myWidth)
                                .append(", \"h\": ").append(c.sprite1.myHeight).append("}");
                    }
                }

                sb.append("}");
                if (i != total - 1) sb.append(",");
                sb.append("\n");
            }
        }

        indent(sb, depth + 1).append("],\n");

        // Optionally embed child widgets recursively (useful if prayer has nested containers)
        indent(sb, depth + 1).append("\"childWidgets\": [\n");
        boolean first = true;
        if (total > 0) {
            for (int i = 0; i < total; i++) {
                int childId = r.children[i];
                if (childId <= 0) continue;
                if (childId >= RSInterface.interfaceCache.length) continue;
                if (RSInterface.interfaceCache[childId] == null) continue;

                if (!first) sb.append(",\n");
                writeInterfaceRecursive(sb, childId, visited, depth + 2);
                first = false;
            }
        }
        sb.append("\n");
        indent(sb, depth + 1).append("]\n");

        indent(sb, depth).append("}");
    }

    private static void num(StringBuilder sb, int indent, String key, int value) {
        indent(sb, indent).append("\"").append(key).append("\": ").append(value).append(",\n");
    }

    private static void bool(StringBuilder sb, int indent, String key, boolean value) {
        indent(sb, indent).append("\"").append(key).append("\": ").append(value).append(",\n");
    }

    private static void str(StringBuilder sb, int indent, String key, String value) {
        if (value == null) value = "";
        indent(sb, indent).append("\"").append(key).append("\": ").append(jsonString(value)).append(",\n");
    }

    private static String jsonString(String s) {
        StringBuilder out = new StringBuilder();
        out.append("\"");
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\\': out.append("\\\\"); break;
                case '"':  out.append("\\\""); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (ch < 32) out.append("?");
                    else out.append(ch);
            }
        }
        out.append("\"");
        return out.toString();
    }

    private static StringBuilder indent(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) sb.append("  ");
        return sb;
    }
}

package org.bukkit.configuration.file;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

/*
Override due to SnakeYaml being a newer version on Bukkit then Sponge
 */
public class YamlConfiguration extends FileConfiguration {

    /**
     * @deprecated
     */
    @Deprecated
    protected static final String COMMENT_PREFIX = "# ";
    /**
     * @deprecated
     */
    @Deprecated
    protected static final String BLANK_CONFIG = "{}\n";
    private final DumperOptions yamlDumperOptions = new DumperOptions();
    private final LoaderOptions yamlLoaderOptions;
    private final YamlConstructor constructor;
    private final YamlRepresenter representer;
    private final Yaml yaml;

    public YamlConfiguration() {
        this.yamlDumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yamlLoaderOptions = new LoaderOptions();
        this.yamlLoaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
        //this.yamlLoaderOptions.setCodePointLimit(Integer.MAX_VALUE); <- too new
        //this.yamlLoaderOptions.setNestingDepthLimit(100);
        this.constructor = new YamlConstructor(this.yamlLoaderOptions);
        this.representer = new YamlRepresenter(this.yamlDumperOptions);
        this.representer.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(this.constructor, this.representer, this.yamlDumperOptions, this.yamlLoaderOptions);
    }

    public @NotNull String saveToString() {
        this.yamlDumperOptions.setIndent(this.options().indent());
        this.yamlDumperOptions.setWidth(this.options().width());
        //this.yamlDumperOptions.setProcessComments(this.options().parseComments());
        MappingNode node = this.toNodeTree(this);
        node.setBlockComments(this.getCommentLines(this.saveHeader(this.options().getHeader()), CommentType.BLOCK));
        node.setEndComments(this.getCommentLines(this.options().getFooter(), CommentType.BLOCK));
        StringWriter writer = new StringWriter();
        if (node.getBlockComments().isEmpty() && node.getEndComments().isEmpty() && node.getValue().isEmpty()) {
            writer.write("");
        } else {
            if (node.getValue().isEmpty()) {
                node.setFlowStyle(DumperOptions.FlowStyle.FLOW);
            }

            this.yaml.serialize(node, writer);
        }

        return writer.toString();
    }

    public void loadFromString(@NotNull String contents) throws InvalidConfigurationException {
        Preconditions.checkArgument(contents != null, "Contents cannot be null");
        //this.yamlLoaderOptions.setProcessComments(this.options().parseComments());
        //this.yamlLoaderOptions.setCodePointLimit(this.options().codePointLimit());

        MappingNode node;
        try {
            Reader reader = new UnicodeReader(new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8)));

            try {
                Node rawNode = this.yaml.compose(reader);

                try {
                    node = (MappingNode) rawNode;
                } catch (ClassCastException var7) {
                    throw new InvalidConfigurationException("Top level is not a Map.");
                }
            } catch (Throwable var8) {
                try {
                    reader.close();
                } catch (Throwable var6) {
                    var8.addSuppressed(var6);
                }

                throw var8;
            }

            reader.close();
        } catch (IOException | ClassCastException | YAMLException var9) {
            Exception e = var9;
            throw new InvalidConfigurationException(e);
        }

        this.map.clear();
        if (node != null) {
            this.adjustNodeComments(node);
            this.options().setHeader(this.loadHeader(this.getCommentLines(node.getBlockComments())));
            this.options().setFooter(this.getCommentLines(node.getEndComments()));
            this.fromNodeTree(node, this);
        }

    }

    private void adjustNodeComments(MappingNode node) {
        if (node.getBlockComments() == null && !node.getValue().isEmpty()) {
            Node firstNode = ((NodeTuple) node.getValue().get(0)).getKeyNode();
            List<CommentLine> lines = firstNode.getBlockComments();
            if (lines != null) {
                int index = -1;

                for (int i = 0; i < lines.size(); ++i) {
                    if (((CommentLine) lines.get(i)).getCommentType() == CommentType.BLANK_LINE) {
                        index = i;
                    }
                }

                if (index != -1) {
                    node.setBlockComments(lines.subList(0, index + 1));
                    firstNode.setBlockComments(lines.subList(index + 1, lines.size()));
                }
            }
        }

    }

    private void fromNodeTree(@NotNull MappingNode input, @NotNull ConfigurationSection section) {
        this.constructor.flattenMapping(input);
        Iterator var3 = input.getValue().iterator();

        while (true) {
            while (var3.hasNext()) {
                NodeTuple nodeTuple = (NodeTuple) var3.next();
                Node key = nodeTuple.getKeyNode();
                String keyString = String.valueOf(this.constructor.construct(key));

                Node value;
                for (value = nodeTuple.getValueNode(); value instanceof AnchorNode; value = ((AnchorNode) value).getRealNode()) {
                }

                if (value instanceof MappingNode && !this.hasSerializedTypeKey((MappingNode) value)) {
                    this.fromNodeTree((MappingNode) value, section.createSection(keyString));
                } else {
                    section.set(keyString, this.constructor.construct(value));
                }

                section.setComments(keyString, this.getCommentLines(key.getBlockComments()));
                if (!(value instanceof MappingNode) && !(value instanceof SequenceNode)) {
                    section.setInlineComments(keyString, this.getCommentLines(value.getInLineComments()));
                } else {
                    section.setInlineComments(keyString, this.getCommentLines(key.getInLineComments()));
                }
            }

            return;
        }
    }

    private boolean hasSerializedTypeKey(MappingNode node) {
        Iterator var2 = node.getValue().iterator();

        while (var2.hasNext()) {
            NodeTuple nodeTuple = (NodeTuple) var2.next();
            Node keyNode = nodeTuple.getKeyNode();
            if (keyNode instanceof ScalarNode) {
                String key = ((ScalarNode) keyNode).getValue();
                if (key.equals("==")) {
                    return true;
                }
            }
        }

        return false;
    }

    private MappingNode toNodeTree(@NotNull ConfigurationSection section) {
        List<NodeTuple> nodeTuples = new ArrayList();

        Node key;
        Object value;
        for (Iterator var3 = section.getValues(false).entrySet().iterator(); var3.hasNext(); nodeTuples.add(new NodeTuple(key, (Node) value))) {
            Map.Entry<String, Object> entry = (Map.Entry) var3.next();
            key = this.representer.represent(entry.getKey());
            if (entry.getValue() instanceof ConfigurationSection) {
                value = this.toNodeTree((ConfigurationSection) entry.getValue());
            } else {
                value = this.representer.represent(entry.getValue());
            }

            key.setBlockComments(this.getCommentLines(section.getComments((String) entry.getKey()), CommentType.BLOCK));
            if (!(value instanceof MappingNode) && !(value instanceof SequenceNode)) {
                ((Node) value).setInLineComments(this.getCommentLines(section.getInlineComments((String) entry.getKey()), CommentType.IN_LINE));
            } else {
                key.setInLineComments(this.getCommentLines(section.getInlineComments((String) entry.getKey()), CommentType.IN_LINE));
            }
        }

        return new MappingNode(Tag.MAP, nodeTuples, DumperOptions.FlowStyle.BLOCK);
    }

    private List<String> getCommentLines(List<CommentLine> comments) {
        List<String> lines = new ArrayList();
        if (comments != null) {
            Iterator var3 = comments.iterator();

            while (var3.hasNext()) {
                CommentLine comment = (CommentLine) var3.next();
                if (comment.getCommentType() == CommentType.BLANK_LINE) {
                    lines.add(null);
                } else {
                    String line = comment.getValue();
                    line = line.startsWith(" ") ? line.substring(1) : line;
                    lines.add(line);
                }
            }
        }

        return lines;
    }

    private List<CommentLine> getCommentLines(List<String> comments, CommentType commentType) {
        List<CommentLine> lines = new ArrayList();
        Iterator var4 = comments.iterator();

        while (var4.hasNext()) {
            String comment = (String) var4.next();
            if (comment == null) {
                lines.add(new CommentLine((Mark) null, (Mark) null, "", CommentType.BLANK_LINE));
            } else {
                String line = comment;
                line = line.isEmpty() ? line : " " + line;
                lines.add(new CommentLine((Mark) null, (Mark) null, line, commentType));
            }
        }

        return lines;
    }

    private List<String> loadHeader(List<String> header) {
        LinkedList<String> list = new LinkedList(header);
        if (!list.isEmpty()) {
            list.removeLast();
        }

        while (!list.isEmpty() && list.peek() == null) {
            list.remove();
        }

        return list;
    }

    private List<String> saveHeader(List<String> header) {
        LinkedList<String> list = new LinkedList(header);
        if (!list.isEmpty()) {
            list.add(null);
        }

        return list;
    }

    public @NotNull YamlConfigurationOptions options() {
        if (this.options == null) {
            this.options = new YamlConfigurationOptions(this);
        }

        return (YamlConfigurationOptions) this.options;
    }

    public static @NotNull org.bukkit.configuration.file.YamlConfiguration loadConfiguration(@NotNull File file) {
        Preconditions.checkArgument(file != null, "File cannot be null");
        org.bukkit.configuration.file.YamlConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException var3) {
        } catch (IOException var4) {
            IOException ex = var4;
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + String.valueOf(file), ex);
        } catch (InvalidConfigurationException var5) {
            InvalidConfigurationException ex = var5;
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + String.valueOf(file), ex);
        }

        return config;
    }

    public static @NotNull org.bukkit.configuration.file.YamlConfiguration loadConfiguration(@NotNull Reader reader) {
        Preconditions.checkArgument(reader != null, "Stream cannot be null");
        org.bukkit.configuration.file.YamlConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();

        try {
            config.load(reader);
        } catch (IOException var3) {
            IOException ex = var3;
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
        } catch (InvalidConfigurationException var4) {
            InvalidConfigurationException ex = var4;
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
        }

        return config;
    }
}


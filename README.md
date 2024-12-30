# Soak-Plugin

Run Bukkit/Spigot/Paper on a Sponge server without hitting compatibility issues with Forge or Sponge.

## Compatibility list

Find the plugins compatibility level on our issues board above.

### Estimated compatibility

currently, Soak is still in the early days and as a result, do not expect many plugins to be supported

## Downloads

Soak will originally release with the ability to only run a single plugin to battle test it for those plugins, but when
its ready it will be downloadable from Sponge's Ore

### I want Soak for all plugins

You can gain a full copy of Soak by either

- building Soak yourself
- Press the actions tab above and download the latest build (GitHub account required)

## How this works

Soak maps Paper methods into Sponge API methods

## So Pore?

in short, yes but newer

Pore was a Sponge plugin that allowed Bukkit plugins to run on older Sponge versions.
Soak is a Sponge plugin that allows Bukkit plugins to run on Sponge API 8+

Pore ended for a (few reasons)[https://caseif.blog/content/post.php?id=41], here are some

### Sponge has become increasing different to Bukkit

this was true around the 1.12.2 time, however Minecraft 1.13 came out and rewrote Sponge a lot and Bukkit a fair bit
too. As a result, both share similarities, allowing for easy mapping.

### Many plugins wont work

The Spigot team are well aware of how many plugins are bypassing the Bukkit api. While the original attempts to move
developers to use just Bukkit API was to mention to use it in the small print of a announcement. The Spigot team have
started to add common methods that developers needed such as the ability to get the players ping though the Bukkit api.
This has lead to more and more plugins using pure Bukkit API code, meaning that Soak will be able to simulate it

#### Paper hard-fork help

With paper's hard fork now in place, the hope is that plugins target Paper's version of the Bukkit-API. PaperMC-API has
more methods than the Spigot-API, these methods can be used in place of interacting directly with NMS.

#### NMS Bounce

The Soak project has a module called ``NMSBounce``, that allows plugins that do interact with NMS to interact with Sponge
code without any changes. This works as CraftBukkit implementations of Bukkit use Mojang mappings to map the original
vanilla server.jar to human-readable code, but CraftBukkit implementations do not map the changes back. While in 
NeoForge, LexForge, Sponge Vanilla and Fabric, they map the changes back. This leaves the package of ``net.minecraft.server``
open for use. There is a little bit of Java trickery to place Soak files in place of ``net.minecraft.server`` (such as
an isolated classloader) but allows for wrapping NMS calls in Sponge API

### Many plugins that do work, wont work with Forge mods

This is still true due to Bukkit's insistence on fixed lists. However, Bukkit now supports Datapacks which can add items
to these fixed lists which breaks the rules of Java, but plugins expect these lists to expand.

#### Changes to Spigot-API

In recent times, the spigot api is relying more on the Vanilla registry rather than fixed lists, this is shown by the
``Materials`` list replacement in ``BlockType`` and ``ItemType``. While this won't fix all instances without breaking 
changes (Paper hard-fork maybe?), it is a start

#### Runtime generated classes

Soak has the ability to generate some classes at runtime, this is used as a last resort, but it allows fixed lists 
(enums) to be generated after mods have loaded, allowing all values to be entered, in term enabling plugins to interact
with modded items

### Sponge is about new beginnings

Soak will only simulate newer plugins in attempt for Sponge being more attractive option for Admins. I must rather see
developers use native Sponge code, but developers tend to target where the users are, and Admins tend to use platforms
where the developers are. By making Sponge more attractive to Admins, it will hopefully help break the cycle that Bukkit
made.



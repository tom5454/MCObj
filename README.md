# MCObj
.obj model loader for Minecraft.

## Installation
1. Install Minecraft Forge for Minecraft 1.14.4  
2. Put MCObj-core and MCObj-base into your .minecraft folder/mods

## Creating your own resourcepack
Put your .obj and .mtl file into `assets/mcobj/models/<block/item>/` folder.  
The models must be exported with the `Triangulate Faces` option to work correctly.  
Create your JSON model and mark your .obj file as parent:
`"parent": "mcobj:<block/item>/<your obj>.obj"`  
You can put fallback model as `<your obj>.obj.json` if mcobj is not installed.  
Tinting:
Set your material name to: `<name>#tint<index>`  
You can use notepad on the exported `.obj` and `.mtl` file or your 3D editor of choice.
Most blocks use tint index 0. (`grass#tint0`)

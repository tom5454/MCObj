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

### Texturing
You have to edit your `.mtl` file with your favourite text editor.  
`newmtl` defines a new material.
`map_Kd` or `map_Ka` or `map_Ks` defines the texture for the material above.
change it to `map_Kd #<id>`.  
```
newmtl <element>
map_Kd #<id>
```
example from the [Spheres Resource Pack](https://github.com/tom5454/MCObj/raw/master/spheres.zip):  
```
newmtl None
Ns 0
Ka 0.000000 0.000000 0.000000
Kd 0.8 0.8 0.8
Ks 0.8 0.8 0.8
d 1
illum 2
map_Kd #north
```
Then in your JSON add  
```json  
"textures": {  
	"id": "<your texture>"  
}  
```  
example:
```json  
"textures": {  
	"north": "block/stone"  
}  
```  

## Entity Models
Create a .json file in `assets/mcobj/models/tesr`, with your model name (`chestmodel.json`).  
```json
{  
	"model": "mcobj:<your model>"  
}  
```  
Put your model into `assets/mcobj/models/redef` folder.  
If your model is an obj file write (`<your model name>.obj`)  
You can find all the model and element names in your minecraft log, after pressing F3+T.  
  
Check out the [Spheres Resourcepack](https://github.com/tom5454/MCObj/raw/master/spheres.zip) for more info.
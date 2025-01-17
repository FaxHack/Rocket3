<div align="center">
   <image src="https://i.imgur.com/D0Yue1c.png" width="400">
</div>
<div align="center">

# Wawa's secret rocket cartel tool

### A Rusherhack plugin for Minecraft Version 1.20.1 - 1.20.6

</div>

## Installation:

1. Move the plugin into the folder ".minecraft\rusherhack\plugins\"
2. Add JVM flag to minecraft instance "-Drusherhack.enablePlugins=true"
3. Launch minecraft with rusherhack installed in the mods folder and the plugin "Rocket3" should appear as a module under "External"

## Usage:

1. Make sure you have a clean inventory (meaning only sugar cane, paper, gunpowder, or firework rockets) when you use the plugin. Otherwise it will not do anything.
2. When you open a crafting table, the plugin will try to craft either paper or fireworks depending on the contents of your inventory.
3. To ensure the plugin always knows what to craft, here is my basic workflow:
  - The "Delay" option should work at 0, but if you run into issues with inconsistancy. Try setting it to ~100-120ms (tested at 60 ping, higher ping could result in a higher need for delay)
  - Have an empty inventory and move a shulker of sugar cane into your inventory
  - Open a crafting table and let it craft a shulker of paper
  - Open the empty shulker from the sugar cane and put 2 rows of paper into it. This should leave your hotbar with a row of paper
  - Move a shulker of gunpowder to your inventory. This should result in 27 stacks of gunpowder and 9 stacks of paper in your inventory
  - Open a crafting table and let it craft a shulker of duration 3 rockets
  - Move the rockets into a new shulker
  - Take a row of paper from the paper shulker and an inventory of gunpowder from a new gunpowder shulker
  - Open a crafting table, craft the rockets and repeat for the last row and for any future shulkers of rockets you want to craft

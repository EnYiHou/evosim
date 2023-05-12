# EVOSIM

## Project Description

EVOSIM is an evolution simulation to show a fight between two opposing species, the prey and predators.

All entities are controlled via their own AI neural network, using Linear Algebra to calculate their next best action based on sensor inputs. Entities can survive,
reproduce, and die by meeting various field conditions. The predators aim to consume the prey in order to accumulate split energy and clone themselves whereas the latter
must survive for as long as possible.

The custom configurations provide a customizable simulation each run, allowing the user to see how certain changeable parameters can affect the outcome.

## Usage

For specific instructions or a detailed list of features , consult the app's [user guide](https://github.com/mattlep11/evosim/blob/main/user-guide.pdf).

### Basic Overview

#### Config Selection
Use the start menu to set the arguments passed into the simulation. Changing them can have a drastic effect on the survival of each species or the strength of their AI.
Each one can be saved as a .json and loaded at any time.

#### Controlling the Map
- W/A/S/D: Pans the camera in the specified direction.
- DRAG: Pans the camera with the mouse.
- +/-/SCROLL: Zooms the camera in and out to a certain limit.
- C: Unzooms the camera to default and centers its position.
- CLICK: Tracks an entity if they were clicked.
- SPACE: Unfollows a currently tracked entity.
- ENTER: Pauses/Unpauses the simulation.
- ESC: Prompts the user to exit the application, does so on confirmation.

#### Settings
See *Help* -> *Modify Preferences*.

---

### Developed by Totally Spies
- ptrstr (Team Coordinator)
- Edelina Alieva
- EnYi Hou
- Matthew Leprohon

#### Evosim

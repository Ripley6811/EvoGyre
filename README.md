# EvoGyre
Gyruss-inspired game written with LibGDX

## Introduction
I wanted to make a game with an interesting projection system like the old arcade
game called Gyruss. This game is the result. The player is flying towards a planet
and swarms of aliens are coming out to stop the player. The single sample level is
about two minutes long.

![alt tag](/EvoGyre.png)

## Gameplay

#### Desktop / HTML controls

Key | Action
--- | ---
**Left-Right arrows** | Move your ship
**Up-arrow** | Toggle green rings on/off
**Down-arrow** | Toggle moving or fixed ship
**Spacebar** or **W** | Fire weapon

#### Screen / Touch controls

Touch | Action
--- | ---
**Bottom left/right arrows** | Move your ship (strafe)
**Top right corner icon** | Toggle moving or fixed ship

Ship fires continuously if Android device is detected.

## Design Notes

#### Projection
All motion calculations are done on a rectangular map that wraps around in the y-direction.
The map positions are projected onto the funnel shape display.
Y ranges from 0 to 360 and corresponds to a mapPosition on the cross-section of the funnel.
X corresponds to the distance down the funnel and does not wrap.

#### Collisions
Bullets are considered as points in collisions and ships will be simple polygons.
Collision interaction is based on the projected point and rendered image size
(Map motion coordinates do not correspond well to image size).

#### Image assets
Spaceship graphics provided by [Udacity](www.udacity.com). Planets and HUD
text created by JWJ in
Photoshop. Title created in Photoshop using two fonts found through Google
Fonts: "Nothing You Could Do" by Kimberly Geswein and "Michroma" by Vernon Adams.
"SHIELDS", "WEAPONS" font is OCR A Extended.
Other special effects designed by JWJ in LibGDX.

#### Audio assets
Audio was created with [CheeseCutter SID Music Editor 2.7](http://theyamo.kapsi.fi/ccutter/about.html)
and edited with [Audacity 2.1.1](http://audacityteam.org/). Intro music is an
excerpt from a piece by [abaddon3AD](https://soundcloud.com/theyamo/kalmankone).


## Credits

- Lead Programmer / Designer - Jay W J
- Special Effects - Jay W J
- Art Assets
    - Spaceships, Bullets, Explosions - Udacity
    - Planets, Text, Buttons - Jay W J
- Intro Music - [abaddon3AD](https://soundcloud.com/theyamo/kalmankone)
- Sound FX - Jay W J

## References
- [LibGDX reference](https://libgdx.badlogicgames.com/nightlies/docs/api/)
- [Tweening algorithms](http://gizma.com/easing/)
- [Equation for point on an ellipse given axes and angle](http://math.stackexchange.com/questions/432902/how-to-get-the-radius-of-an-ellipse-at-a-specific-angle-by-knowing-its-semi-majo)
- [How to make a planet (in PhotoShop)](http://www.solarvoyager.com/images/tutorials/planet_tutorial_large.jpg)
    - [30+ Photoshop Tutorials For Creating Space And Planets](http://naldzgraphics.net/tutorials/30-photoshop-tutorials-for-creating-space-and-planets/)
- [Google Fonts search](https://www.google.com/fonts)


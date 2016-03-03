# EvoGyre
Gyruss-inspired game written with LibGDX

## Introduction


## Gameplay
Key | Action
--- | ---
**Left-Right arrows** | Move your ship
**Up-arrow** | Toggle green rings on/off
**Down-arrow** | Toggle moving or fixed ship
**W** | Fire weapon
**1 to 4** | (temporary) Try different weapon layouts



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
Photoshop. Other special effects designed by JWJ in LibGDX.
Title created in Photoshop using two fonts found through Google Fonts: "Nothing You Could Do" by Kimberly Geswein and "Michroma" by Vernon Adams.


## Credits

- Creative Lead - Jay W J
- Special Effects - Jay W J
- Art Assets
    - Spaceships & Bullets - Udacity
    - Planets & Text - Jay W J
- Music - Jay W J
- Sound Effects - Jay W J

## References
- [LibGDX reference](https://libgdx.badlogicgames.com/nightlies/docs/api/)
- [Tweening algorithms](http://gizma.com/easing/)
- [Equation for point on an ellipse given axes and angle](http://math.stackexchange.com/questions/432902/how-to-get-the-radius-of-an-ellipse-at-a-specific-angle-by-knowing-its-semi-majo)
- [How to make a planet (in PhotoShop)](http://www.solarvoyager.com/images/tutorials/planet_tutorial_large.jpg)
    - [30+ Photoshop Tutorials For Creating Space And Planets](http://naldzgraphics.net/tutorials/30-photoshop-tutorials-for-creating-space-and-planets/)
- [Google Fonts search](https://www.google.com/fonts)


## TODO
- [x] Add boss
- [x] Add ship explosions
- [ ] !!! Score and End game screen
- [ ] !!! Android accelerometer controls
- [x] Indicator for shield strength
- [x] Add shield and weapon powerups
- [x] Opening screen and options
- [ ] Add missiles (Optional)
- [ ] Sound FX
- [x] Refactor back to not use static members of GameScreen

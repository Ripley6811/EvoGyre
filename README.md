# EvoGyre
Gyruss-inspired game written with LibGDX

## Introduction


## Gameplay
**Left-Right arrows** - Move your ship
**Up-arrow** - Toggle green rings on/off
**Down-arrow** - Toggle moving or fixed ship
**W** - Fire weapon
**1 to 4** - (temporary) Try different weapon layouts



## Design Notes

#### Projection
All motion calculations are done on a rectangular map that wraps around in the y-direction.
The map positions are projected onto the funnel shape display.
Y ranges from 0 to 360 and corresponds to a position on the cross-section of the funnel.
X corresponds to the distance down the funnel and does not wrap.

#### Collisions
Bullets are considered as points in collisions and ships will be simple polygons.
Collision interaction is based on the projected point and rendered image size
(Map motion coordinates do not correspond well to image size).

#### Image assets
Spaceship graphics provided by [Udacity](www.udacity.com). Planets created by JWJ in
Photoshop. Other special effects designed by JWJ in LibGDX.


## Credits

- Creative Lead - Jay W J
- Special Effects - Jay W J
- Art Assets
    - Spaceships & Bullets - Udacity
    - Planets - Jay W J
- Music - Jay W J
- Sound Effects - Jay W J

## References
- [LibGDX reference](https://libgdx.badlogicgames.com/nightlies/docs/api/)
- [Tweening algorithms](http://gizma.com/easing/)
- [Equation for point on an ellipse given axes and angle](http://math.stackexchange.com/questions/432902/how-to-get-the-radius-of-an-ellipse-at-a-specific-angle-by-knowing-its-semi-majo)
- [How to make a planet (in PhotoShop)](http://www.solarvoyager.com/images/tutorials/planet_tutorial_large.jpg)
    - [30+ Photoshop Tutorials For Creating Space And Planets](http://naldzgraphics.net/tutorials/30-photoshop-tutorials-for-creating-space-and-planets/)

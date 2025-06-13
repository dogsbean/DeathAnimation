# MMC Death Animation

A plugin that replicates the **Minemen Club** style death animation.
If you need support, contact me on Discord: `krouda`

### Credits

* ClubSpigot
* BananaSpigot

### How to Use

1. **Patch your Spigot build**
   Apply [this patch](https://gist.github.com/dogsbean/9aac69cc00ada079f43f5c8ca29b5861) to your Spigot source.

2. **Build and run**
   Compile and run your Spigot server with the patched source.
   This plugin requires the patch to function correctly.

### Integration with PotPvP-SI

If you're using PotPvP-SI:

1. Remove the existing `animateDeath` method.
2. Create a new method named `fakeDeath`, and use it instead of `animateDeath`.
3. In the `addSpectator` method, delay the call to `updateVisibility` by **20 ticks**.

> Preview:
> ![Preview](https://github.com/user-attachments/assets/ec2c8ac1-843e-4859-a794-208c049d7849)

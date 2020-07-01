# [1.0.0-alpha.6](https://github.com/Silthus/art-framework/compare/v1.0.0-alpha.5...v1.0.0-alpha.6) (2020-07-01)


### Features

* add option to run action after a delay ([29ba665](https://github.com/Silthus/art-framework/commit/29ba665ce0438204bf4fdf67676e4ad27029a240))
* add the option to delay trigger ([8e3cd87](https://github.com/Silthus/art-framework/commit/8e3cd87458c72a4b3580dd67b96457196c2c3a4b))

# [1.0.0-alpha.5](https://github.com/Silthus/art-framework/compare/v1.0.0-alpha.4...v1.0.0-alpha.5) (2020-06-30)


### Features

* actions and requirements now accept the Target object ([767fb54](https://github.com/Silthus/art-framework/commit/767fb54f408c2008c8a274d095b164d250148ceb))


### BREAKING CHANGES

* the text(...) and execute(...) methods of Requirements and Actions must be changed to accept a Target<TSource>, e.g. Target<Player>

# [1.0.0-alpha.4](https://github.com/Silthus/art-framework/compare/v1.0.0-alpha.3...v1.0.0-alpha.4) (2020-06-30)


### Bug Fixes

* java 8 compilation errors ([8c70e69](https://github.com/Silthus/art-framework/commit/8c70e6981fe4d1c6b1b364a16cecaab24d0e3ead))
* more java 8 compilation errors ([d77721b](https://github.com/Silthus/art-framework/commit/d77721b894abdad2263bc8f436909d2fb70eae35))
* **example:** ?location requirement always returns true ([600d364](https://github.com/Silthus/art-framework/commit/600d36471f326eca832a1b2fbfe87fbba086fc59))


### Features

* add advanced trigger parsing ([f20fede](https://github.com/Silthus/art-framework/commit/f20fede852d313a33ee15348f49c5c572299713c))
* add bStats metrics ([0295a18](https://github.com/Silthus/art-framework/commit/0295a18831d9857491bd14d61a0ffc5bda473e27))
* add scheduler abstraction for delegating delayed tasks ([d9d88ff](https://github.com/Silthus/art-framework/commit/d9d88ff5d206d4b0e946b07a066f7f6a73b6af55))
* add trigger edge cases ([3f4d576](https://github.com/Silthus/art-framework/commit/3f4d5766aefe14e74e09e38f462b564e96faf528))
* add trigger listener to ArtResult ([fa132ca](https://github.com/Silthus/art-framework/commit/fa132cade19019099b782cdf24f0a9a01f3fe94c))
* add trigger registration and rework registration builder ([6ab1eb4](https://github.com/Silthus/art-framework/commit/6ab1eb47e1b42f2246cfd782e51da91bbf2dfc24))
* downgrade to java 8 to support more servers ([0b8d4f3](https://github.com/Silthus/art-framework/commit/0b8d4f3eacb087e7e7d1139b76eba0ae792b5a31))
* parse and execute trigger ([2641d3c](https://github.com/Silthus/art-framework/commit/2641d3c107cd7fb96e9e9d90324d5e4ccb601b8f))
* print art with config information at startup ([12feb95](https://github.com/Silthus/art-framework/commit/12feb954900e379449cadfb40ab8a350a8bbd7e9))


### Reverts

* add bStats metrics ([e321a00](https://github.com/Silthus/art-framework/commit/e321a00073cceab647d1368ab868103757989eb4))

# [1.0.0-alpha.3](https://github.com/Silthus/art-framework/compare/v1.0.0-alpha.2...v1.0.0-alpha.3) (2020-06-25)


### Features

* update to Minecraft/Spigot 1.16.1 ([0f1e238](https://github.com/Silthus/art-framework/commit/0f1e23845dc060d49de9190d6945354d0779bdf5))

# [1.0.0-alpha.2](https://github.com/Silthus/art-framework/compare/v1.0.0-alpha.1...v1.0.0-alpha.2) (2020-06-25)


### Features

* add ArtResult tests ([2e86a00](https://github.com/Silthus/art-framework/commit/2e86a008527a7edbab7557d78101ad760b4a2460))
* add nested actions and requirements for actions ([8b0c902](https://github.com/Silthus/art-framework/commit/8b0c902751252efddadae2d24de9bdc185268439))
* add requirements and requirement parser ([bdf0a74](https://github.com/Silthus/art-framework/commit/bdf0a746735a15d932489260b9f78e0677a51abb))
* global and local ArtResult filter ([cdfb42b](https://github.com/Silthus/art-framework/commit/cdfb42bbc3796578af61dff71fd6621672522cd6))
* test requirements before executing action ([11c3189](https://github.com/Silthus/art-framework/commit/11c3189f4452a5a573377767034509154f4b09a1))

# 1.0.0-alpha.1 (2020-06-20)

This is the first release and only contains the actions feature.

> All API and content is subject to change and may break without notice from pre-release to pre-release.

**Do not use this on a production server until 1.0.0 is released!**

### Features

* add advanced logging printing the error config filename ([6d1079d](https://github.com/Silthus/art-framework/commit/6d1079d4c9a196a985f008f241350e8de644685c))
* **release:** replace versions in files on release ([1fd8ab1](https://github.com/Silthus/art-framework/commit/1fd8ab1a646ed72683eab017fec721e06f957c11))
* first working action example and parsing ([1410796](https://github.com/Silthus/art-framework/commit/1410796dced2028d69fc566a3129551e5cf5b5e4))
* **art:** add requirements and corresponding builder ([a4cc27f](https://github.com/Silthus/art-framework/commit/a4cc27f487efdd7b09da9648a8dbb5403b15eb85))
* return ARTResult instead of art object list ([c627c88](https://github.com/Silthus/art-framework/commit/c627c889fe719b644f5da85101f6c3d4905d06de))

### BREAKING CHANGES

* ART.create(ARTConfig) now returns an ARTResult instead of a List<ARTObject>.

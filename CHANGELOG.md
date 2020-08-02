# [1.0.0-beta.2](https://github.com/art-framework/art-core/compare/v1.0.0-beta.1...v1.0.0-beta.2) (2020-08-02)


### Bug Fixes

* remove unneeded immutable annotation ([530aaef](https://github.com/art-framework/art-core/commit/530aaef293a2a16ea4b1bd2814e46af5360e1976))

# 1.0.0-beta.1 (2020-08-02)


### Bug Fixes

* ambiguous trigger method overload ([7f1a7c3](https://github.com/art-framework/art-core/commit/7f1a7c38d06743aa46e6d1b030eed4e611431b47))
* java 8 compilation errors ([8c70e69](https://github.com/art-framework/art-core/commit/8c70e6981fe4d1c6b1b364a16cecaab24d0e3ead))
* more java 8 compilation errors ([d77721b](https://github.com/art-framework/art-core/commit/d77721b894abdad2263bc8f436909d2fb70eae35))
* overloading of the trigger varargs methods ([30c6ffb](https://github.com/art-framework/art-core/commit/30c6ffb637b1c27aa1d4ae88b9ac7e455dae3d97))
* trigger parsing and execution ([cd4b1d6](https://github.com/art-framework/art-core/commit/cd4b1d661655e5a8bda25e198e24eea6bda2da16))
* **build:** grant execute perm to scripts ([0275f9a](https://github.com/art-framework/art-core/commit/0275f9a4b54ac19d3e951f5e3f9dea00299fd3ac))
* **build:** grant execute perm to scripts ([e0bd68b](https://github.com/art-framework/art-core/commit/e0bd68b475c969eb4606992a680cecf803255341))
* **build:** grant execute perm to scripts in release step ([b792e66](https://github.com/art-framework/art-core/commit/b792e66960bbcad2fcc42918c454fa04c6a2148a))
* **build:** move bukkit tasks into sub project ([8ab28b7](https://github.com/art-framework/art-core/commit/8ab28b786017f95165b6b3ff070ebf96984030ca))
* **docs:** use readthedocs theme ([71e991b](https://github.com/art-framework/art-core/commit/71e991b065d6dbf9fb9ffac4ba417da370ab7e5d))
* **example:** ?location requirement always returns true ([600d364](https://github.com/art-framework/art-core/commit/600d36471f326eca832a1b2fbfe87fbba086fc59))
* target java 11 ([e29e93c](https://github.com/art-framework/art-core/commit/e29e93c315acdbaed197a478893dc4fc658bfe64))


### Features

* replace readthedocs with docsify ([6635be7](https://github.com/art-framework/art-core/commit/6635be7baa76b0f99fa55306e32874be760717e2))
* **parser:** add option to define primitive arrays ([579f415](https://github.com/art-framework/art-core/commit/579f41506d04a8d6a17410e8c9a5ed95b93c7ee7))
* add alias mappings ([b95e14f](https://github.com/art-framework/art-core/commit/b95e14f8d29c9992846ab797f2a78a91a774a37c))
* add ebean persistence layer ([7c040ab](https://github.com/art-framework/art-core/commit/7c040abb20ef7a5773db632342d882ab38c15ef9))
* add ebean persistence layer ([ec1908f](https://github.com/art-framework/art-core/commit/ec1908fa712c4020eb2034b0295753c3550db565))
* add first draft of hiberate connector ([a181bae](https://github.com/art-framework/art-core/commit/a181bae47e5684545165603abad5c80923cedb63))
* add MessageSender and default targets ([b8c78c0](https://github.com/art-framework/art-core/commit/b8c78c0113dccdfa0cb03dbabd282052a709717c))
* add the option to store data via the context ([a3a52c4](https://github.com/art-framework/art-core/commit/a3a52c471289eba73dd910dbe15440bd6b5ecc79))
* combine annotations into a single @ArtObject and @ConfigOption annotation ([e8e54ff](https://github.com/art-framework/art-core/commit/e8e54ff9f669b877e95c755ee30d64c9fbc48f96))
* finish ebean persistence layer ([db530bd](https://github.com/art-framework/art-core/commit/db530bdfa7b7ce179c52240b5440ba0738ffa277))
* **action:** add execute_once option to actions ([203f7d0](https://github.com/art-framework/art-core/commit/203f7d0c727dd993acdf16d2c086f567a662cb92))
* **action:** add execute_once option to actions ([20b775b](https://github.com/art-framework/art-core/commit/20b775b01998f97729ccd8963b2ed90da84a10ac))
* **actions:** actions now can have a cooldown ([010fc83](https://github.com/art-framework/art-core/commit/010fc837e0186dcd5d02ddbd7aa0e54abc9a2ff1)), closes [#7](https://github.com/art-framework/art-core/issues/7)
* **actions:** actions now can have a cooldown ([853f2e8](https://github.com/art-framework/art-core/commit/853f2e83a59dac8e14b8ebd0fee9b48d4df0e590)), closes [#7](https://github.com/art-framework/art-core/issues/7)
* **art:** add requirements and corresponding builder ([a4cc27f](https://github.com/art-framework/art-core/commit/a4cc27f487efdd7b09da9648a8dbb5403b15eb85))
* **config:** add BukkitArtConfig to load from a ConfigurationSection ([3baac1d](https://github.com/art-framework/art-core/commit/3baac1d6400733b7ce9bb373632329a3e667e349))
* **config:** add BukkitArtConfig to load from a ConfigurationSection ([b56f5a3](https://github.com/art-framework/art-core/commit/b56f5a3e9b24b23347ca3235522227826fec4aa8))
* **docs:** publish mkdocs to gh-pages ([411c48e](https://github.com/art-framework/art-core/commit/411c48eae71e814adeb454bc1c85cff6bfeeaf80))
* **release:** replace versions in files on release ([1fd8ab1](https://github.com/art-framework/art-core/commit/1fd8ab1a646ed72683eab017fec721e06f957c11))
* **requirements:** add check_once, count and negated options ([f5cf864](https://github.com/art-framework/art-core/commit/f5cf86434fb47d6f2da2b1a5e5dbb2bce1e52678))
* **requirements:** add check_once, count and negated options ([61b47f9](https://github.com/art-framework/art-core/commit/61b47f9cc6f803536445d04286092a84a259d8df))
* **storage:** add storage api with default memory provider ([79e0fa9](https://github.com/art-framework/art-core/commit/79e0fa999c338567d8e68de589e77fe5a4cb054d)), closes [#7](https://github.com/art-framework/art-core/issues/7)
* **storage:** add storage api with default memory provider ([4999de9](https://github.com/art-framework/art-core/commit/4999de96f7cc4ec678a0e4db620018ef81506782)), closes [#7](https://github.com/art-framework/art-core/issues/7)
* **trigger:** add cooldown and execute_once to trigger ([96bc6e6](https://github.com/art-framework/art-core/commit/96bc6e6703edc3733584d52f18a6226b94ace14b))
* **trigger:** add cooldown and execute_once to trigger ([9f49c50](https://github.com/art-framework/art-core/commit/9f49c501c2c467472fe239de11b0815b495f3397))
* **trigger:** add options to prevent action execution on trigger ([d79c366](https://github.com/art-framework/art-core/commit/d79c3667997d6c0b4aa00229f82cebc9348d2e9d))
* **trigger:** add options to prevent action execution on trigger ([6534f12](https://github.com/art-framework/art-core/commit/6534f12d22f2c6b42ff26a4685fa3f209b528356))
* **trigger:** add options to prevent action execution on trigger ([144b043](https://github.com/art-framework/art-core/commit/144b0431b24c0f3ad5cbbd042397a756d617fa10))
* actions and requirements now accept the Target object ([767fb54](https://github.com/art-framework/art-core/commit/767fb54f408c2008c8a274d095b164d250148ceb))
* add advanced logging printing the error config filename ([6d1079d](https://github.com/art-framework/art-core/commit/6d1079d4c9a196a985f008f241350e8de644685c))
* add advanced trigger parsing ([f20fede](https://github.com/art-framework/art-core/commit/f20fede852d313a33ee15348f49c5c572299713c))
* add ArtResult tests ([2e86a00](https://github.com/art-framework/art-core/commit/2e86a008527a7edbab7557d78101ad760b4a2460))
* add bStats metrics ([0295a18](https://github.com/art-framework/art-core/commit/0295a18831d9857491bd14d61a0ffc5bda473e27))
* add first draft of hiberate connector ([8d5c1a0](https://github.com/art-framework/art-core/commit/8d5c1a0a714f90b7378bf235f6504461db21d149))
* add nested actions and requirements for actions ([8b0c902](https://github.com/art-framework/art-core/commit/8b0c902751252efddadae2d24de9bdc185268439))
* add option to run action after a delay ([29ba665](https://github.com/art-framework/art-core/commit/29ba665ce0438204bf4fdf67676e4ad27029a240))
* add requirements and requirement parser ([bdf0a74](https://github.com/art-framework/art-core/commit/bdf0a746735a15d932489260b9f78e0677a51abb))
* add scheduler abstraction for delegating delayed tasks ([d9d88ff](https://github.com/art-framework/art-core/commit/d9d88ff5d206d4b0e946b07a066f7f6a73b6af55))
* add the option to delay trigger ([8e3cd87](https://github.com/art-framework/art-core/commit/8e3cd87458c72a4b3580dd67b96457196c2c3a4b))
* add trigger edge cases ([3f4d576](https://github.com/art-framework/art-core/commit/3f4d5766aefe14e74e09e38f462b564e96faf528))
* add trigger listener to ArtResult ([fa132ca](https://github.com/art-framework/art-core/commit/fa132cade19019099b782cdf24f0a9a01f3fe94c))
* add trigger registration and rework registration builder ([6ab1eb4](https://github.com/art-framework/art-core/commit/6ab1eb47e1b42f2246cfd782e51da91bbf2dfc24))
* downgrade to java 8 to support more servers ([0b8d4f3](https://github.com/art-framework/art-core/commit/0b8d4f3eacb087e7e7d1139b76eba0ae792b5a31))
* first working action example and parsing ([1410796](https://github.com/art-framework/art-core/commit/1410796dced2028d69fc566a3129551e5cf5b5e4))
* global and local ArtResult filter ([cdfb42b](https://github.com/art-framework/art-core/commit/cdfb42bbc3796578af61dff71fd6621672522cd6))
* parse and execute trigger ([2641d3c](https://github.com/art-framework/art-core/commit/2641d3c107cd7fb96e9e9d90324d5e4ccb601b8f))
* print art with config information at startup ([12feb95](https://github.com/art-framework/art-core/commit/12feb954900e379449cadfb40ab8a350a8bbd7e9))
* test requirements before executing action ([11c3189](https://github.com/art-framework/art-core/commit/11c3189f4452a5a573377767034509154f4b09a1))
* update to Minecraft/Spigot 1.16.1 ([0f1e238](https://github.com/art-framework/art-core/commit/0f1e23845dc060d49de9190d6945354d0779bdf5))


### Reverts

* add bStats metrics ([e321a00](https://github.com/art-framework/art-core/commit/e321a00073cceab647d1368ab868103757989eb4))
* upload codacy coverage ([f8598d6](https://github.com/art-framework/art-core/commit/f8598d6554ab1c6960da7583e68a8a9ce01e9b4f))
* upload codacy coverage ([9cf53e0](https://github.com/art-framework/art-core/commit/9cf53e050d0724bb4f9bf9d55cdbafa617090139))


* feat!: return ARTResult instead of art object list ([c627c88](https://github.com/art-framework/art-core/commit/c627c889fe719b644f5da85101f6c3d4905d06de))


### BREAKING CHANGES

* the text(...) and execute(...) methods of Requirements and Actions must be changed to accept a Target<TSource>, e.g. Target<Player>
* ART.create(ARTConfig) now returns an ARTResult instead of a List<ARTObject>.

# [1.0.0-alpha.12](https://github.com/art-framework/art-framework/compare/v1.0.0-alpha.11...v1.0.0-alpha.12) (2020-07-31)

> ## Important  
> This update is a major rewrite of the complete API and codebase. Almost everything has a **BREAKING CHANGE**.

The ART-Framework now uses a fluent style for all API endpoints and enforces a scoped configuration approach, which allows changing and configuring every part of the framework. The design is heavily inspired by the [jOOQ](https://github.com/jOOQ/jOOQ) library.

### Bug Fixes

* ambiguous trigger method overload ([7f1a7c3](https://github.com/art-framework/art-framework/commit/7f1a7c38d06743aa46e6d1b030eed4e611431b47))
* trigger parsing and execution ([cd4b1d6](https://github.com/art-framework/art-framework/commit/cd4b1d661655e5a8bda25e198e24eea6bda2da16))


### Features

* replace readthedocs with docsify ([6635be7](https://github.com/art-framework/art-framework/commit/6635be7baa76b0f99fa55306e32874be760717e2))
* **parser:** add option to define primitive arrays ([579f415](https://github.com/art-framework/art-framework/commit/579f41506d04a8d6a17410e8c9a5ed95b93c7ee7))

# [1.0.0-alpha.11](https://github.com/art-framework/art-framework/compare/v1.0.0-alpha.10...v1.0.0-alpha.11) (2020-07-10)


### Features

* add alias mappings ([b95e14f](https://github.com/art-framework/art-framework/commit/b95e14f8d29c9992846ab797f2a78a91a774a37c))
* combine annotations into a single @ArtObject and @ConfigOption annotation ([e8e54ff](https://github.com/art-framework/art-framework/commit/e8e54ff9f669b877e95c755ee30d64c9fbc48f96))

# [1.0.0-alpha.10](https://github.com/art-framework/art-framework/compare/v1.0.0-alpha.9...v1.0.0-alpha.10) (2020-07-10)


### Features

* add ebean persistence layer ([7c040ab](https://github.com/art-framework/art-framework/commit/7c040abb20ef7a5773db632342d882ab38c15ef9))
* add ebean persistence layer ([ec1908f](https://github.com/art-framework/art-framework/commit/ec1908fa712c4020eb2034b0295753c3550db565))
* add first draft of hiberate connector ([a181bae](https://github.com/art-framework/art-framework/commit/a181bae47e5684545165603abad5c80923cedb63))
* add first draft of hiberate connector ([8d5c1a0](https://github.com/art-framework/art-framework/commit/8d5c1a0a714f90b7378bf235f6504461db21d149))
* finish ebean persistence layer ([db530bd](https://github.com/art-framework/art-framework/commit/db530bdfa7b7ce179c52240b5440ba0738ffa277))
* **action:** add execute_once option to actions ([20b775b](https://github.com/art-framework/art-framework/commit/20b775b01998f97729ccd8963b2ed90da84a10ac))
* **actions:** actions now can have a cooldown ([853f2e8](https://github.com/art-framework/art-framework/commit/853f2e83a59dac8e14b8ebd0fee9b48d4df0e590)), closes [#7](https://github.com/art-framework/art-framework/issues/7)
* **config:** add BukkitArtConfig to load from a ConfigurationSection ([b56f5a3](https://github.com/art-framework/art-framework/commit/b56f5a3e9b24b23347ca3235522227826fec4aa8))
* **requirements:** add check_once, count and negated options ([61b47f9](https://github.com/art-framework/art-framework/commit/61b47f9cc6f803536445d04286092a84a259d8df))
* **storage:** add storage api with default memory provider ([4999de9](https://github.com/art-framework/art-framework/commit/4999de96f7cc4ec678a0e4db620018ef81506782)), closes [#7](https://github.com/art-framework/art-framework/issues/7)
* **trigger:** add cooldown and execute_once to trigger ([9f49c50](https://github.com/art-framework/art-framework/commit/9f49c501c2c467472fe239de11b0815b495f3397))


### Reverts

* upload codacy coverage ([9cf53e0](https://github.com/art-framework/art-framework/commit/9cf53e050d0724bb4f9bf9d55cdbafa617090139))

# [1.0.0-alpha.9](https://github.com/art-framework/art-framework/compare/v1.0.0-alpha.8...v1.0.0-alpha.9) (2020-07-09)


### Bug Fixes

* **docs:** use readthedocs theme ([71e991b](https://github.com/art-framework/art-framework/commit/71e991b065d6dbf9fb9ffac4ba417da370ab7e5d))


### Features

* add MessageSender and default targets ([b8c78c0](https://github.com/art-framework/art-framework/commit/b8c78c0113dccdfa0cb03dbabd282052a709717c))
* add the option to store data via the context ([a3a52c4](https://github.com/art-framework/art-framework/commit/a3a52c471289eba73dd910dbe15440bd6b5ecc79))
* **docs:** publish mkdocs to gh-pages ([411c48e](https://github.com/art-framework/art-framework/commit/411c48eae71e814adeb454bc1c85cff6bfeeaf80))
* **trigger:** add options to prevent action execution on trigger ([144b043](https://github.com/art-framework/art-framework/commit/144b0431b24c0f3ad5cbbd042397a756d617fa10))

# [1.0.0-alpha.8](https://github.com/art-framework/art-framework/compare/v1.0.0-alpha.7...v1.0.0-alpha.8) (2020-07-06)


### Features

* **trigger:** add options to prevent action execution on trigger ([d79c366](https://github.com/art-framework/art-framework/commit/d79c3667997d6c0b4aa00229f82cebc9348d2e9d))
* **trigger:** add options to prevent action execution on trigger ([6534f12](https://github.com/art-framework/art-framework/commit/6534f12d22f2c6b42ff26a4685fa3f209b528356))

# [1.0.0-alpha.7](https://github.com/art-framework/art-framework/compare/v1.0.0-alpha.6...v1.0.0-alpha.7) (2020-07-06)


### Features

* **action:** add execute_once option to actions ([203f7d0](https://github.com/art-framework/art-framework/commit/203f7d0c727dd993acdf16d2c086f567a662cb92))
* **actions:** actions now can have a cooldown ([010fc83](https://github.com/art-framework/art-framework/commit/010fc837e0186dcd5d02ddbd7aa0e54abc9a2ff1)), closes [#7](https://github.com/art-framework/art-framework/issues/7)
* **config:** add BukkitArtConfig to load from a ConfigurationSection ([3baac1d](https://github.com/art-framework/art-framework/commit/3baac1d6400733b7ce9bb373632329a3e667e349))
* **requirements:** add check_once, count and negated options ([f5cf864](https://github.com/art-framework/art-framework/commit/f5cf86434fb47d6f2da2b1a5e5dbb2bce1e52678))
* **storage:** add storage api with default memory provider ([79e0fa9](https://github.com/art-framework/art-framework/commit/79e0fa999c338567d8e68de589e77fe5a4cb054d)), closes [#7](https://github.com/art-framework/art-framework/issues/7)
* **trigger:** add cooldown and execute_once to trigger ([96bc6e6](https://github.com/art-framework/art-framework/commit/96bc6e6703edc3733584d52f18a6226b94ace14b))


### Reverts

* upload codacy coverage ([f8598d6](https://github.com/art-framework/art-framework/commit/f8598d6554ab1c6960da7583e68a8a9ce01e9b4f))

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

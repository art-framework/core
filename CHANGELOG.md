# 1.0.0-alpha.1 (2020-06-19)


### Bug Fixes

* **build:** grant execute perm to scripts ([0275f9a](https://github.com/Silthus/art-framework/commit/0275f9a4b54ac19d3e951f5e3f9dea00299fd3ac))
* **build:** grant execute perm to scripts ([e0bd68b](https://github.com/Silthus/art-framework/commit/e0bd68b475c969eb4606992a680cecf803255341))
* **build:** grant execute perm to scripts in release step ([b792e66](https://github.com/Silthus/art-framework/commit/b792e66960bbcad2fcc42918c454fa04c6a2148a))
* target java 11 ([e29e93c](https://github.com/Silthus/art-framework/commit/e29e93c315acdbaed197a478893dc4fc658bfe64))
* **build:** move bukkit tasks into sub project ([8ab28b7](https://github.com/Silthus/art-framework/commit/8ab28b786017f95165b6b3ff070ebf96984030ca))


### Features

* add advanced logging printing the error config filename ([6d1079d](https://github.com/Silthus/art-framework/commit/6d1079d4c9a196a985f008f241350e8de644685c))
* **release:** replace versions in files on release ([1fd8ab1](https://github.com/Silthus/art-framework/commit/1fd8ab1a646ed72683eab017fec721e06f957c11))
* first working action example and parsing ([1410796](https://github.com/Silthus/art-framework/commit/1410796dced2028d69fc566a3129551e5cf5b5e4))
* **art:** add requirements and corresponding builder ([a4cc27f](https://github.com/Silthus/art-framework/commit/a4cc27f487efdd7b09da9648a8dbb5403b15eb85))


* feat!: return ARTResult instead of art object list ([c627c88](https://github.com/Silthus/art-framework/commit/c627c889fe719b644f5da85101f6c3d4905d06de))


### BREAKING CHANGES

* ART.create(ARTConfig) now returns an ARTResult instead of a List<ARTObject>.

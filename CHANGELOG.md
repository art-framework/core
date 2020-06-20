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

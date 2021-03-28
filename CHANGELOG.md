# [5.0.0](https://github.com/art-framework/core/compare/v4.5.4...v5.0.0) (2021-03-28)


### Bug Fixes

* cyclic dependency graph check not working ([f3a5d16](https://github.com/art-framework/core/commit/f3a5d16e6ebfd229356ce67ea4580ff10e48f987))
* onBootstrap not called after modules have been enabled ([6d39b4e](https://github.com/art-framework/core/commit/6d39b4ef819c3f09b9e1c65d4b355c7f3bd4a639))
* remove methods that can directly create a scope to avoid misuse and cleanup api surface ([9f46e2d](https://github.com/art-framework/core/commit/9f46e2dd29ed2af64e4a743a9ed7b567535bb715))


### Code Refactoring

* removed old @On... lifecycle phase annotations in favor of strongly typed modules implementing Module ([32459a9](https://github.com/art-framework/core/commit/32459a9fe6992f5b33f59f9558d0ccf6944e38d5))


### Features

* add new module interface with strongly typed lifecycle methods ([f7c3978](https://github.com/art-framework/core/commit/f7c39783eaaf5ac50c42910cd81950f88a170e96))
* add the option to disable automatic module registration within the ArtModule annotation ([4b1da6b](https://github.com/art-framework/core/commit/4b1da6be74fe732b4bbb78f34fb04bc46b32212f))
* allow registration of modules on the fly without relying on magic methods ([b3591e4](https://github.com/art-framework/core/commit/b3591e4115a0313c74fd6cbf059e44e5055ddb4c))
* automatically register the bootstrap scope as a global scope ([1cd6016](https://github.com/art-framework/core/commit/1cd60163e6fc5f55eba6b678478710d26862aefa))
* include the placeholderapi replacements module in the bukkit platform ([fc78038](https://github.com/art-framework/core/commit/fc78038e980ae34afd4bde67acd425ddfdce223c))


### BREAKING CHANGES

* the @On... annotations have been removed in favor of implementing Module
* all modules must now extend Module and no longer need the @On... annotations
* the ART.bootstrap(BootstrapScope) method now automatically registers the scope as a global scope. Use the ART.bootstrap(BootstrapScope, boolean) method to prevent that.
* removed Scope.defaultScope() and Scope.of(...) methods as they should not be used to obtain a scope. Directly create a scope for use in tests: new DefaultScope() or use the default global scope ART.globalScope().

## [4.5.4](https://github.com/art-framework/art-framework/compare/v4.5.3...v4.5.4) (2021-02-11)


### Bug Fixes

* load art in onenable phase ([b9873a9](https://github.com/art-framework/art-framework/commit/b9873a9dd1eaea66dd5080ac08a8893bd4af017c))

## [4.5.3](https://github.com/art-framework/art-framework/compare/v4.5.2...v4.5.3) (2021-02-11)


### Bug Fixes

* generic actions and requirements not auto loaded ([e5aca43](https://github.com/art-framework/art-framework/commit/e5aca43759a23c6c7783b835523f3834f15d0f19))
* use reflections to safely scan classpath ([970c14f](https://github.com/art-framework/art-framework/commit/970c14fa490e8e2557d3393c561af314f04590bd))

## [4.5.2](https://github.com/art-framework/art-framework/compare/v4.5.1...v4.5.2) (2021-02-11)


### Bug Fixes

* load and enable all art after all other plugins are loaded ([6bb89df](https://github.com/art-framework/art-framework/commit/6bb89df1f77170b515d0823e7c4c41688d636487))

## [4.5.1](https://github.com/art-framework/art-framework/compare/v4.5.0...v4.5.1) (2021-02-08)


### Bug Fixes

* add missing location and entity damage trigger ([e7b2d5d](https://github.com/art-framework/art-framework/commit/e7b2d5dff90e994db860655bbdb09683d7f46e87))
* allow empty flow lists when parsing art ([5b91261](https://github.com/art-framework/art-framework/commit/5b9126167d7637712df70bbc2c12521d3b15fa34))
* default finder not all registered ([c60f1c6](https://github.com/art-framework/art-framework/commit/c60f1c6fac8416de55db12205ad06a4373279e28))
* equals and hashcode of artcontext not correct ([ba6c240](https://github.com/art-framework/art-framework/commit/ba6c240ff58155fc313750d6d5da1390d1adaffa))

# [4.5.0](https://github.com/art-framework/art-framework/compare/v4.4.0...v4.5.0) (2021-02-01)


### Features

* auto apply prefix of module in same code source ([ee30e8c](https://github.com/art-framework/art-framework/commit/ee30e8c4abc0833b90cf733a5f392ab07b401198))

# [4.4.0](https://github.com/art-framework/art-framework/compare/v4.3.0...v4.4.0) (2021-02-01)


### Features

* add execute command action and parser alias ([52eb3aa](https://github.com/art-framework/art-framework/commit/52eb3aa104962f0e72e03f476d5fd9ed0cebc397))
* add PermissionRequirement ([72ee6cf](https://github.com/art-framework/art-framework/commit/72ee6cfc25b20d1a92c3c2d6a5441e787de79f35))

# [4.3.0](https://github.com/art-framework/art-framework/compare/v4.2.0...v4.3.0) (2021-01-31)


### Bug Fixes

* automatic registration of art and modules ([2263a27](https://github.com/art-framework/art-framework/commit/2263a27b799b9e40826eea91ff71bb6df3298a93))
* automatic resolver registration failing ([0684cae](https://github.com/art-framework/art-framework/commit/0684cae0bec67318bddeaef7dc4e1fbf67f93b1b))
* remove unused reflections library ([10e5f11](https://github.com/art-framework/art-framework/commit/10e5f11a861d52cbaa00ec131a169aa443456524))
* soft depend on ebean-wrapper and fallback to map storage provider ([125f460](https://github.com/art-framework/art-framework/commit/125f460858258a551336a9195c26bc0e3362ea50))


### Features

* add ModifierMatcher to ease the development of pattern matches ([99d64f6](https://github.com/art-framework/art-framework/commit/99d64f69b676b3d8ae3d758758a4db15f4252b93))
* automatically register replacements found in the class path ([5a68b11](https://github.com/art-framework/art-framework/commit/5a68b11eb579ccd9b3dae24636549ca5e4154cdb))
* automatically register resolvers found in the classpath ([d949d41](https://github.com/art-framework/art-framework/commit/d949d4186c17e01d9b9eba506687a09acaa2f44a))

# [4.2.0](https://github.com/art-framework/art-framework/compare/v4.1.1...v4.2.0) (2021-01-29)


### Features

* add give item action ([21932ba](https://github.com/art-framework/art-framework/commit/21932ba10530659480b6f188c520536fae969ee7))

## [4.1.1](https://github.com/art-framework/art-framework/compare/v4.1.0...v4.1.1) (2021-01-28)


### Bug Fixes

* maven/gradle jitpack reference ([036883c](https://github.com/art-framework/art-framework/commit/036883cb718ce69bbc5ad9b59ec59bbf7c2a70d2))

# [4.1.0](https://github.com/art-framework/art-framework/compare/v4.0.0...v4.1.0) (2021-01-28)


### Features

* migrate packages to io.art-framework:api package ([190804b](https://github.com/art-framework/art-framework/commit/190804b9b1a206a8d45ad35196fd8a417e12f20c))

# [4.0.0](https://github.com/art-framework/core/compare/v3.1.0...v4.0.0) (2021-01-27)


### Bug Fixes

* do not execute next actions if error occured ([35c7faa](https://github.com/art-framework/core/commit/35c7faa0c103ff9d0b26c6fa1442d5362b0107c6))
* only replace string values ([c962277](https://github.com/art-framework/core/commit/c96227769a8b02bffff249436893608f42699b12))
* return scope after BootstrapPhase has ended ([78a788c](https://github.com/art-framework/core/commit/78a788ce0728fe098a7fb13b9fd4812a1275f145))
* string array variables not replaced ([96a724a](https://github.com/art-framework/core/commit/96a724a706787108d16a97c82b83dc258b29dced))


### Code Refactoring

* remove unused events api and cleanup code ([caec71b](https://github.com/art-framework/core/commit/caec71bbe8d7a738195228a9d113f5de51b2bd4a))


### Features

* auto replace variables in a config map ([c81f0ae](https://github.com/art-framework/core/commit/c81f0ae6d24b3122dfed5a1932b46a9a96733d01))
* **config:** add debug flag in configuration to increase log level ([d355357](https://github.com/art-framework/core/commit/d3553578ee484e6f214a9c2a7fb0457ba16b2555))
* **config:** add debug log output to action context ([e92f1a1](https://github.com/art-framework/core/commit/e92f1a1c08590f341289c0e515db1f7bcdda2ca0))
* add replacement of ${player} with player name ([66deedf](https://github.com/art-framework/core/commit/66deedfd32c9117c02f68c86698a7f118a99d32b)), closes [#17](https://github.com/art-framework/core/issues/17)
* add replacement of variables when calling resolve ([25d9d6d](https://github.com/art-framework/core/commit/25d9d6db7245f6dfd12b7a455558a5c55200fbb6)), closes [#17](https://github.com/art-framework/core/issues/17)
* add variables to the execution and artcontext ([a133ce1](https://github.com/art-framework/core/commit/a133ce152826f202742fc7ae8af011b7e74f5cb3)), closes [#17](https://github.com/art-framework/core/issues/17)
* bootstrap the art-framework onLoad and call onLoad when enabled and enable after 1 tick ([6f86061](https://github.com/art-framework/core/commit/6f860617d52378b5462fc416169f1054e29a0a8d))


### BREAKING CHANGES

* removed unused event api

# [3.1.0](https://github.com/art-framework/core/compare/v3.0.1...v3.1.0) (2021-01-25)


### Bug Fixes

* allow creation of context with factory or direct art object instance ([819fe51](https://github.com/art-framework/core/commit/819fe5100720bfa9f7458b909304c40db5b730e4))
* purge generic info from default resolvers ([b925270](https://github.com/art-framework/core/commit/b925270f9d01992e8db0402a7174891b9337244c))
* **config:** extraneous keys and values overwrite explicit matching key values ([8eb8d59](https://github.com/art-framework/core/commit/8eb8d59c65c667fb3b21167c7dc9b3f78dacaf96))


### Features

* add generic enum resolver as default resolver ([6a068ce](https://github.com/art-framework/core/commit/6a068ce413a81d9d191ed5f8bf62eed97550e50c))
* add Material resolver ([92395af](https://github.com/art-framework/core/commit/92395aff5142fa90da143323d4435edbaecda566))
* add resolver to config parser regex using $(...) syntax ([d3a7ff1](https://github.com/art-framework/core/commit/d3a7ff1bc7266d7f15863fca483708ef79595ac2))
* finalize resolver api ([cf7dff2](https://github.com/art-framework/core/commit/cf7dff28c6214611f3a4be861e3f5fbe0fbc29e5))
* **api:** add resolver api ([15c119b](https://github.com/art-framework/core/commit/15c119b956370c5fdb4677ca647345860c9ca7e6)), closes [#16](https://github.com/art-framework/core/issues/16)
* **api:** factories now have a method to create instances of art objects ([1e2436a](https://github.com/art-framework/core/commit/1e2436a3efa05a1fdae82ff91ab5390b5983fa47))
* **api:** implement ResolverFactory and ResolverProvider ([aea9db8](https://github.com/art-framework/core/commit/aea9db83cdcf5113e81daf52441a2d8529c346f3)), closes [#16](https://github.com/art-framework/core/issues/16)
* **bukkit:** add equipment requirement ([40be1d8](https://github.com/art-framework/core/commit/40be1d88a7f0b13b6a6f895401257bdf24515758))

## [3.0.1](https://github.com/art-framework/core/compare/v3.0.0...v3.0.1) (2021-01-23)


### Bug Fixes

* **config:** recursively set fields in super class ([9170171](https://github.com/art-framework/core/commit/91701718b78d25efd2afa8f86a3db61381f891db))

# [3.0.0](https://github.com/art-framework/core/compare/v2.2.0...v3.0.0) (2021-01-23)


### Bug Fixes

* return alias mapping in all() factories method ([248e613](https://github.com/art-framework/core/commit/248e613b0644b4e3dfc9cd9c8d75bce2a6c78098))
* **rewards:** do no try to parse an empty global rewards list ([e98328c](https://github.com/art-framework/core/commit/e98328c29c37363a55d78979212330fce315012c))


### Features

* add new and improved trigger api ([a56876e](https://github.com/art-framework/core/commit/a56876ea7cf0b743728783cac89793510bab6fca))
* completely refactor and fix trigger api ([8868a1e](https://github.com/art-framework/core/commit/8868a1ec8af360f132be0b308e71f1a84529fa05))
* migrate bukkit module to new trigger api ([1917133](https://github.com/art-framework/core/commit/1917133cd575043445cdd3f9daba131190df4d22))


### BREAKING CHANGES

* everything that has todo with triggers needs to be reimplemented

# [2.2.0](https://github.com/art-framework/core/compare/v2.1.1...v2.2.0) (2021-01-18)


### Bug Fixes

* **ebean:** improve ebean persistence caching layer ([08f237e](https://github.com/art-framework/core/commit/08f237e454f2036bb6a5cfbe6dfc13b22907581a))


### Features

* add location trigger and requirement ([7f5b6bc](https://github.com/art-framework/core/commit/7f5b6bca60eb574162aa1771a48822f7d53a4343))

## [2.1.1](https://github.com/art-framework/core/compare/v2.1.0...v2.1.1) (2021-01-18)


### Bug Fixes

* return null provider if not existent ([70e5126](https://github.com/art-framework/core/commit/70e5126057e0c04c4f5bb54bfcd96245abd356c9))

# [2.1.0](https://github.com/art-framework/core/compare/v2.0.0...v2.1.0) (2021-01-18)


### Features

* **api:** add option to register custom providers ([d07a176](https://github.com/art-framework/core/commit/d07a1764a764a1a78f6a464bd759e1315ee5c238))
* **api:** improve flow line parser api ([4ad42db](https://github.com/art-framework/core/commit/4ad42dbdc519d65d3ab9b680ec3a61422c8c1cb6))

# [2.0.0](https://github.com/art-framework/core/compare/v1.6.6...v2.0.0) (2021-01-18)


### Features

* **api:** remove multi trigger listener targets to simplify api ([ff830bc](https://github.com/art-framework/core/commit/ff830bc7f8346aa59c598613f63341b63d6c2ab5))


### BREAKING CHANGES

* **api:** ArtContext#onTrigger(...) now takes a TriggerListener with a single target instead of an array.

## [1.6.6](https://github.com/art-framework/core/compare/v1.6.5...v1.6.6) (2021-01-18)


### Bug Fixes

* **release:** publish javadocs from correct path ([eb44a3d](https://github.com/art-framework/core/commit/eb44a3d57f511de745a6ce26d6fef1d49eed0669))
* **release:** update group and package name ([4a3a3ea](https://github.com/art-framework/core/commit/4a3a3ea61576f8ab8700feaabb34561204721754))

## [1.6.5](https://github.com/art-framework/art-core/compare/v1.6.4...v1.6.5) (2021-01-18)


### Bug Fixes

* **release:** move api into separate module ([81adf4b](https://github.com/art-framework/art-core/commit/81adf4b6d80302912f729efa2001e58e9a69baf6))

## [1.6.4](https://github.com/art-framework/art-core/compare/v1.6.3...v1.6.4) (2021-01-18)


### Bug Fixes

* **release:** publish scripts artifact as well ([88204ea](https://github.com/art-framework/art-core/commit/88204ea2d99d7045fc13dcdf5422dd91c9443b82))

## [1.6.3](https://github.com/art-framework/art-core/compare/v1.6.2...v1.6.3) (2021-01-17)


### Bug Fixes

* **release:** publish bukkit project ([d63a283](https://github.com/art-framework/art-core/commit/d63a283ba1ba62a91ac62fa5920b601b754450fe))

## [1.6.2](https://github.com/art-framework/art-core/compare/v1.6.1...v1.6.2) (2021-01-17)


### Bug Fixes

* **release:** release all artifacts ([81395c7](https://github.com/art-framework/art-core/commit/81395c7c2a0636bbb50ce1f7afc73c48459fff10))

## [1.6.1](https://github.com/art-framework/art-core/compare/v1.6.0...v1.6.1) (2021-01-17)


### Bug Fixes

* **release:** only publish main project ([efe566c](https://github.com/art-framework/art-core/commit/efe566c12f8c67a2000c805f6b871daf53f39801))

# [1.6.0](https://github.com/art-framework/art-core/compare/v1.5.0...v1.6.0) (2021-01-17)


### Features

* **release:** bump version until 1.5.0 for a stable release ([32ba9fb](https://github.com/art-framework/art-core/commit/32ba9fbe962d025fbc827ad683127b3efdf5d95f))

# [1.5.0](https://github.com/art-framework/art-core/compare/v1.4.0...v1.5.0) (2021-01-17)


### Features

* **release:** bump version until 1.5.0 for a stable release ([5eb5309](https://github.com/art-framework/art-core/commit/5eb530913ac2a0592453a235ce3802694013a97d))

# [1.4.0](https://github.com/art-framework/art-core/compare/v1.3.0...v1.4.0) (2021-01-17)


### Features

* **release:** bump version until 1.5.0 for a stable release ([eb3c2f5](https://github.com/art-framework/art-core/commit/eb3c2f583d4eb4456d421c323c0a4ca181f6582e))

# [1.3.0](https://github.com/art-framework/art-core/compare/v1.2.3...v1.3.0) (2021-01-17)

### Bug Fixes

* **release:** cleanup changelog ([2c897e8](https://github.com/art-framework/art-core/commit/2c897e8b57cf9d82fec6f4a4a3cd4c78811f41d1))
* **release:** update publish config ([ab8dd02](https://github.com/art-framework/art-core/commit/ab8dd02a432f42abe1e89f7b298f479390d5e51f))
* **store:** unboxing of int to Integer fails ([5f2100b](https://github.com/art-framework/art-core/commit/5f2100b872032b04b8c5f57d76fb9fdc0ea4337f))
* persistence cache not working ([b942d5a](https://github.com/art-framework/art-core/commit/b942d5a8d56780077d9f3be8c5455b4d3dc75d3c))
* register scripts after loading ([dcc340d](https://github.com/art-framework/art-core/commit/dcc340db7b40e9f0029524ab5d2fa45e77f2def0))


### Features

* **trigger:** add option to filter [@damage](https://github.com/damage) event by player or entity ([54477c9](https://github.com/art-framework/art-core/commit/54477c91e7d543395d864ce8ccc8eeeb0f811160))
* add count option to triggers ([3c6b64d](https://github.com/art-framework/art-core/commit/3c6b64d9919f2c42cae7e890fcdae11e138bff26))
* migrate the script module and include it in bukkit by default ([4b52b3e](https://github.com/art-framework/art-core/commit/4b52b3edc1829755ba37c46f31a34f2cfc37346b))
* split metadata persistence table into separate columns ([255e0e2](https://github.com/art-framework/art-core/commit/255e0e2efda6e767c0061b134e5cec7b6c2f4c5d))

### Performance Improvements

* cache counter and perform database update async ([3e44e1f](https://github.com/art-framework/art-core/commit/3e44e1f2a11d51bb70033ec50cc7d2d8c76d07d1))

## [1.2.3](https://github.com/art-framework/art-core/compare/v1.2.2...v1.2.3) (2021-01-16)

### Bug Fixes

* unwrap optional trigger target ([4cfe479](https://github.com/art-framework/art-core/commit/4cfe47954791f0f0fe4f7363fcfa34e9715c9a6a))

## [1.2.2](https://github.com/art-framework/art-core/compare/v1.2.1...v1.2.2) (2021-01-16)


### Bug Fixes

* register targets with the art-framework ([17ea3d9](https://github.com/art-framework/art-core/commit/17ea3d98939d0f0915280886bf499d90f4bc0ab9))
* unwrap trigger target into target ([66317ad](https://github.com/art-framework/art-core/commit/66317ad6b1c69297d0798880db430d50d7578fa9))

## [1.2.1](https://github.com/art-framework/art-core/compare/v1.2.0...v1.2.1) (2021-01-16)


### Bug Fixes

* **db:** rename persistence columns ([cf1608b](https://github.com/art-framework/art-core/commit/cf1608b4eb75b6cee0af549978c9a92556b37c58))

# [1.2.0](https://github.com/art-framework/art-core/compare/v1.1.0...v1.2.0) (2021-01-16)


### Bug Fixes

* add migrations ([595fd6a](https://github.com/art-framework/art-core/commit/595fd6a3798e7f4b9bf334be758b747cd408d9d1))


### Features

* integrate storage provider into bukkit module ([8838ff2](https://github.com/art-framework/art-core/commit/8838ff277378bad38fec33b4af40d5387402f3c3))

# [1.1.0](https://github.com/art-framework/art-core/compare/v1.0.2...v1.1.0) (2021-01-16)


### Bug Fixes

* register basic bukkit art ([9ac32e7](https://github.com/art-framework/art-core/commit/9ac32e7c3996ba3ccb59ad879ff1c1024c0f2f01))


### Features

* add option to parse input and provide a storage key ([a204792](https://github.com/art-framework/art-core/commit/a204792c5f7f90d0eff787f94aeca9659e36e642))

## [1.0.2](https://github.com/art-framework/art-core/compare/v1.0.1...v1.0.2) (2021-01-15)


### Bug Fixes

* **build:** use jdk11 for jitpack builds ([dd2342d](https://github.com/art-framework/art-core/commit/dd2342d1fd7e77b79a459c64182c8494eea544d7))

## [1.0.1](https://github.com/art-framework/art-core/compare/v1.0.0...v1.0.1) (2021-01-15)


### Bug Fixes

* auto loading art in modules causes security conflicts with class loader ([e0a2ab1](https://github.com/art-framework/art-core/commit/e0a2ab11a758be2468f4a33f20ca00587e9abe6d))
* autoload all art setting does nothing ([b3c8fdc](https://github.com/art-framework/art-core/commit/b3c8fdc422640a3f0a3c341dea0d9c46e4cb96b2))

# 1.0.0 (2021-01-15)


### Bug Fixes

* **art-context:** stackoverflow in test method ([e19a289](https://github.com/art-framework/art-core/commit/e19a289e375b07c382a06a5bfb8fd5c48459ef54))
* add default object target for generic action and requirements ([4d954df](https://github.com/art-framework/art-core/commit/4d954df943f3d5d3997c5867452b18da9032afbf))
* ambiguous trigger method overload ([7f1a7c3](https://github.com/art-framework/art-core/commit/7f1a7c38d06743aa46e6d1b030eed4e611431b47))
* create bootstrap config inside base dir ([1a4067b](https://github.com/art-framework/art-core/commit/1a4067b05d975a22be2fcf6a6d6f0a1126004ae1))
* create configs parent dir and not config as dir ([4be0e68](https://github.com/art-framework/art-core/commit/4be0e6811626278778e2c07cd3811b816637561f))
* create dirs before saving config ([b64beac](https://github.com/art-framework/art-core/commit/b64beac61bd6a7375b1f1524e0779db25c1de932))
* do not register duplicate trigger ([ae19358](https://github.com/art-framework/art-core/commit/ae19358e09921d855987e251024999b068fb1ec8))
* ignore unknown fields on config deserialization ([a1b43e3](https://github.com/art-framework/art-core/commit/a1b43e3f298ccac0b77ce192211503cb8e56dcf1))
* java 8 compilation errors ([8c70e69](https://github.com/art-framework/art-core/commit/8c70e6981fe4d1c6b1b364a16cecaab24d0e3ead))
* make module path sub path of basePath ([3fbb777](https://github.com/art-framework/art-core/commit/3fbb77747c1e8b163272963a3dcd919fa7185fa8))
* more java 8 compilation errors ([d77721b](https://github.com/art-framework/art-core/commit/d77721b894abdad2263bc8f436909d2fb70eae35))
* only calc module meta equals with identifier and class ([5c91b1f](https://github.com/art-framework/art-core/commit/5c91b1f81e6994da5fd485003f9acd2676040507))
* overloading of the trigger varargs methods ([30c6ffb](https://github.com/art-framework/art-core/commit/30c6ffb637b1c27aa1d4ae88b9ac7e455dae3d97))
* pretty print array config string ([ed726cb](https://github.com/art-framework/art-core/commit/ed726cb22449c056f090db52fa7774fbaf8ccb93))
* register bootstrap module as global scope ([7963d1d](https://github.com/art-framework/art-core/commit/7963d1d9a795d2f73b9c8482a171530fd911a232))
* **build:** move bukkit tasks into sub project ([8ab28b7](https://github.com/art-framework/art-core/commit/8ab28b786017f95165b6b3ff070ebf96984030ca))
* **modules:** catch all module errors inside downstream methods ([f5d46b4](https://github.com/art-framework/art-core/commit/f5d46b4885d147c0f14e7fef79ea86017d41b121))
* reload configs from disk when module is reloaded ([8974391](https://github.com/art-framework/art-core/commit/89743914db2bc93f78d25a1d6a6c403a87afee96))
* remove unneeded immutable annotation ([530aaef](https://github.com/art-framework/art-core/commit/530aaef293a2a16ea4b1bd2814e46af5360e1976))
* targets extending AbstractTarget are not detected ([32f3ba4](https://github.com/art-framework/art-core/commit/32f3ba437144814914c1ef4c3fd283b7cf889373))
* trigger parsing and execution ([cd4b1d6](https://github.com/art-framework/art-core/commit/cd4b1d661655e5a8bda25e198e24eea6bda2da16))
* update configuration of scope in bootstrapping phase after each module ([eb72cd2](https://github.com/art-framework/art-core/commit/eb72cd234cc41fe37070b12b8068b1ed75999e3a))
* use art object provider to create config instance for mapping fields ([6e3dfb2](https://github.com/art-framework/art-core/commit/6e3dfb22ddd60fbc47d4d2ef2de7ccdbc829bbf6))
* **build:** grant execute perm to scripts ([0275f9a](https://github.com/art-framework/art-core/commit/0275f9a4b54ac19d3e951f5e3f9dea00299fd3ac))
* **build:** grant execute perm to scripts ([e0bd68b](https://github.com/art-framework/art-core/commit/e0bd68b475c969eb4606992a680cecf803255341))
* **build:** grant execute perm to scripts in release step ([b792e66](https://github.com/art-framework/art-core/commit/b792e66960bbcad2fcc42918c454fa04c6a2148a))
* **docs:** use readthedocs theme ([71e991b](https://github.com/art-framework/art-core/commit/71e991b065d6dbf9fb9ffac4ba417da370ab7e5d))
* **example:** ?location requirement always returns true ([600d364](https://github.com/art-framework/art-core/commit/600d36471f326eca832a1b2fbfe87fbba086fc59))
* **modules:** create new instance of module class on register ([a645266](https://github.com/art-framework/art-core/commit/a645266c5a0595a653369904f45ab5222dcdc692))
* **release:** update yarn.lock and remove husky ([9ccc838](https://github.com/art-framework/art-core/commit/9ccc83853abd3dcd0569da03b99411481ccbac43))
* **trigger:** provide result creation methods ([29aeab3](https://github.com/art-framework/art-core/commit/29aeab315db7eea3fb69ffbd96423dbbe43f820a))
* target java 11 ([e29e93c](https://github.com/art-framework/art-core/commit/e29e93c315acdbaed197a478893dc4fc658bfe64))


### Code Refactoring

* make all methods fluent ([bcff317](https://github.com/art-framework/art-core/commit/bcff3176189eeb3412d73e0068761eb804fde098))
* make Configuration immutable and only creatable by a builder ([c2a8686](https://github.com/art-framework/art-core/commit/c2a86863345a146d47234cd5d787f93bff19862f))
* migrate settings into the Scope ([e57cf17](https://github.com/art-framework/art-core/commit/e57cf17b2e152d27f4e46a6538a5ee5c163579b4))
* rename @ArtModule to @Module ([9dc8494](https://github.com/art-framework/art-core/commit/9dc84947302ad1e8bc989bd67892b8ebe819093e))


### Features

* **module:** add basic action logging module ([ad15ee1](https://github.com/art-framework/art-core/commit/ad15ee18f16cdc11f09a60ca376b9bfc5e4d6c66))
* add alias mappings ([b95e14f](https://github.com/art-framework/art-core/commit/b95e14f8d29c9992846ab797f2a78a91a774a37c))
* add autoRegister option to @ART annotation ([e5f5911](https://github.com/art-framework/art-core/commit/e5f591136320a814623f5d8057c2208a70b4239d))
* add convenience load(...) method to the Configuration ([a08bb35](https://github.com/art-framework/art-core/commit/a08bb35a49958c684623935710b1fe881c0c42b7))
* add cyclic dependency resolver and module resolution ([33d4270](https://github.com/art-framework/art-core/commit/33d4270a66fb525b115c670bcad54cd274fbf3c7))
* add ebean persistence layer ([7c040ab](https://github.com/art-framework/art-core/commit/7c040abb20ef7a5773db632342d882ab38c15ef9))
* add ebean persistence layer ([ec1908f](https://github.com/art-framework/art-core/commit/ec1908fa712c4020eb2034b0295753c3550db565))
* add fasterxml jackson yaml config provider ([4f5e742](https://github.com/art-framework/art-core/commit/4f5e7424c965914f3045f790e70be593b83d10a3))
* add finder provider and art object finder ([2441f70](https://github.com/art-framework/art-core/commit/2441f707345b4425e7d1495df3d555ab5faf8611))
* add first draft of hiberate connector ([a181bae](https://github.com/art-framework/art-core/commit/a181bae47e5684545165603abad5c80923cedb63))
* add MessageSender and default targets ([b8c78c0](https://github.com/art-framework/art-core/commit/b8c78c0113dccdfa0cb03dbabd282052a709717c))
* add module interface and annotations ([bb79800](https://github.com/art-framework/art-core/commit/bb79800e4d9f07bebfe42194c92e287429718dc1))
* add module interface and annotations ([b1e995f](https://github.com/art-framework/art-core/commit/b1e995fd95e2943414a1947f99f54e86165f57a1))
* add ModuleFinder ([aae0e0b](https://github.com/art-framework/art-core/commit/aae0e0b427780fe92f948b173fb53fe5278f9f7a))
* add remove methods to the FactoryProvider ([55b2ffb](https://github.com/art-framework/art-core/commit/55b2ffb96cf7d3f32c51babe1ab739ef9fdaf995))
* add target and artobject finder ([5200954](https://github.com/art-framework/art-core/commit/5200954902fd987e3204cd365e554cfeb2f69ab0))
* add temporary data storage provider ([7cbbba9](https://github.com/art-framework/art-core/commit/7cbbba92dd0514a73948edef987e182abeaf8f9e))
* add the option to enable and disable trigger manually ([c48ea65](https://github.com/art-framework/art-core/commit/c48ea65e20b397bf94df92edaf5059d151b0300f))
* add the option to provide a trigger with a provider ([a491708](https://github.com/art-framework/art-core/commit/a49170877d782d7ead7c1cc0b044c89afb8c6fe6))
* add the option to store data via the context ([a3a52c4](https://github.com/art-framework/art-core/commit/a3a52c471289eba73dd910dbe15440bd6b5ecc79))
* add the option to use a custom module resolver ([deb99a7](https://github.com/art-framework/art-core/commit/deb99a7a38e3b5eeb213aa74bcf3b4abe89c65a8))
* allow @Config tag on fields ([f339193](https://github.com/art-framework/art-core/commit/f339193127390c3978dc93838b9259983ec06b8a))
* allow adding targets to the current execution context ([ef9c4a8](https://github.com/art-framework/art-core/commit/ef9c4a82b5440787c36e139aaabea63437f73875))
* create and inject config fields on module creation ([dadcbb5](https://github.com/art-framework/art-core/commit/dadcbb5b43858b88f309ed0829c1057911f44ef3))
* create and load the config from the module directory ([461f6fd](https://github.com/art-framework/art-core/commit/461f6fd620406c48a9fc43ff12fe4eed98ce2a57))
* implement dynamic module loading system ([119b68e](https://github.com/art-framework/art-core/commit/119b68eb8582271581269799e89ec5b29daba6ca))
* load all art from modules that are not explicitly tagged with @ArtModule ([5d592be](https://github.com/art-framework/art-core/commit/5d592be0bc1ac7b81a266dac8a86edb9e7c2bacf))
* log registered art and config at registration ([ddce31b](https://github.com/art-framework/art-core/commit/ddce31b42cc122b26de208bea148cc3991144d29))
* provide a way for modules to store data ([45743a3](https://github.com/art-framework/art-core/commit/45743a3fce16a2065d09f82b88fba9185b3e0a82))
* **action:** add execute_once option to actions ([203f7d0](https://github.com/art-framework/art-core/commit/203f7d0c727dd993acdf16d2c086f567a662cb92))
* **action:** add execute_once option to actions ([20b775b](https://github.com/art-framework/art-core/commit/20b775b01998f97729ccd8963b2ed90da84a10ac))
* **actions:** actions now can have a cooldown ([010fc83](https://github.com/art-framework/art-core/commit/010fc837e0186dcd5d02ddbd7aa0e54abc9a2ff1)), closes [#7](https://github.com/art-framework/art-core/issues/7)
* **actions:** actions now can have a cooldown ([853f2e8](https://github.com/art-framework/art-core/commit/853f2e83a59dac8e14b8ebd0fee9b48d4df0e590)), closes [#7](https://github.com/art-framework/art-core/issues/7)
* **config:** add BukkitArtConfig to load from a ConfigurationSection ([3baac1d](https://github.com/art-framework/art-core/commit/3baac1d6400733b7ce9bb373632329a3e667e349))
* **config:** add BukkitArtConfig to load from a ConfigurationSection ([b56f5a3](https://github.com/art-framework/art-core/commit/b56f5a3e9b24b23347ca3235522227826fec4aa8))
* **docs:** publish mkdocs to gh-pages ([411c48e](https://github.com/art-framework/art-core/commit/411c48eae71e814adeb454bc1c85cff6bfeeaf80))
* **modules:** add option to provide package names to scan for art ([b49d460](https://github.com/art-framework/art-core/commit/b49d46098142e3db8908ba24266a42f05f8e0421))
* **modules:** any class can now be registered as module ([a9bac73](https://github.com/art-framework/art-core/commit/a9bac7333ba67d875e0c0404c458321aa5ee0521))
* **modules:** create bootstrap lifecycle for modules ([5767233](https://github.com/art-framework/art-core/commit/57672339c9ee56166da1d980d1cd767e7ae5a309))
* combine annotations into a single @ArtObject and @ConfigOption annotation ([e8e54ff](https://github.com/art-framework/art-core/commit/e8e54ff9f669b877e95c755ee30d64c9fbc48f96))
* finish ebean persistence layer ([db530bd](https://github.com/art-framework/art-core/commit/db530bdfa7b7ce179c52240b5440ba0738ffa277))
* split bootstrapping from primary scope ([9040b2a](https://github.com/art-framework/art-core/commit/9040b2a0152134dddf5e23bc36f84f3214ca7cd1))
* **modules:** add bootstrap lifecycle method ([f931936](https://github.com/art-framework/art-core/commit/f931936b5b767325945b8bb3e04bc2a5e3c3ae13))
* refactor Configuration to live in a Scope ([8aa171b](https://github.com/art-framework/art-core/commit/8aa171b2f619fe6efd3da229698556f27b2b2597))
* register modules with annotations only ([c9d5573](https://github.com/art-framework/art-core/commit/c9d5573e35b75959dba748aff59c3a27e868c06f))
* replace readthedocs with docsify ([6635be7](https://github.com/art-framework/art-core/commit/6635be7baa76b0f99fa55306e32874be760717e2))
* **parser:** add option to define primitive arrays ([579f415](https://github.com/art-framework/art-core/commit/579f41506d04a8d6a17410e8c9a5ed95b93c7ee7))
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
* **art:** add requirements and corresponding builder ([a4cc27f](https://github.com/art-framework/art-core/commit/a4cc27f487efdd7b09da9648a8dbb5403b15eb85))
* **release:** replace versions in files on release ([1fd8ab1](https://github.com/art-framework/art-core/commit/1fd8ab1a646ed72683eab017fec721e06f957c11))


### Reverts

* "refactor: rename @ArtModule to @Module" ([f37b23e](https://github.com/art-framework/art-core/commit/f37b23ee3d7208fa9f9ac3066c767997c672a6e6))
* add bStats metrics ([e321a00](https://github.com/art-framework/art-core/commit/e321a00073cceab647d1368ab868103757989eb4))
* undo module structure update ([e991cfc](https://github.com/art-framework/art-core/commit/e991cfcbcf94b5daed6512843edf66702ea6bf31))
* upload codacy coverage ([f8598d6](https://github.com/art-framework/art-core/commit/f8598d6554ab1c6960da7583e68a8a9ce01e9b4f))
* upload codacy coverage ([9cf53e0](https://github.com/art-framework/art-core/commit/9cf53e050d0724bb4f9bf9d55cdbafa617090139))


* feat!: return ARTResult instead of art object list ([c627c88](https://github.com/art-framework/art-core/commit/c627c889fe719b644f5da85101f6c3d4905d06de))


### BREAKING CHANGES

* settings are no longer inside the configuration and must be accessed directly from the scope

Signed-off-by: Michael Reichenbach <michael@reichenbach.in>
* @ArtModule is now called @Module

Signed-off-by: Michael Reichenbach <michael@reichenbach.in>
* every art class now takes a scope instead of a configuration. The scope is now the main entry point into the art-framework.

Signed-off-by: Michael Reichenbach <michael@reichenbach.in>
* the Configuration, Settings and ArtSettings can now only be created with their respective builder.
* refactored and cleaned up a lot of non fluent methods making them fluent.

Signed-off-by: Michael Reichenbach <michael@reichenbach.in>
* removed the ArtModule interface and replaced it with the @ArtModule, @OnLoad, @OnEnable and @OnDisable annotations.

Signed-off-by: Michael Reichenbach <michael@reichenbach.in>
* triggers in the art context must now be enabled manually for them to work. The reason for this is unwanted side effects of ART configs that should not contain trigger.
* the text(...) and execute(...) methods of Requirements and Actions must be changed to accept a Target<TSource>, e.g. Target<Player>
* ART.create(ARTConfig) now returns an ARTResult instead of a List<ARTObject>.

# [1.0.0-beta.31](https://github.com/art-framework/art-core/compare/v1.0.0-beta.30...v1.0.0-beta.31) (2020-10-30)


### Features

* **module:** add basic action logging module ([ad15ee1](https://github.com/art-framework/art-core/commit/ad15ee18f16cdc11f09a60ca376b9bfc5e4d6c66))

# [1.0.0-beta.30](https://github.com/art-framework/art-core/compare/v1.0.0-beta.29...v1.0.0-beta.30) (2020-09-07)


### Bug Fixes

* **art-context:** stackoverflow in test method ([e19a289](https://github.com/art-framework/art-core/commit/e19a289e375b07c382a06a5bfb8fd5c48459ef54))

# [1.0.0-beta.29](https://github.com/art-framework/art-core/compare/v1.0.0-beta.28...v1.0.0-beta.29) (2020-09-02)


### Bug Fixes

* register bootstrap module as global scope ([7963d1d](https://github.com/art-framework/art-core/commit/7963d1d9a795d2f73b9c8482a171530fd911a232))

# [1.0.0-beta.28](https://github.com/art-framework/art-core/compare/v1.0.0-beta.27...v1.0.0-beta.28) (2020-09-02)


### Bug Fixes

* create bootstrap config inside base dir ([1a4067b](https://github.com/art-framework/art-core/commit/1a4067b05d975a22be2fcf6a6d6f0a1126004ae1))

# [1.0.0-beta.27](https://github.com/art-framework/art-core/compare/v1.0.0-beta.26...v1.0.0-beta.27) (2020-09-02)


### Bug Fixes

* pretty print array config string ([ed726cb](https://github.com/art-framework/art-core/commit/ed726cb22449c056f090db52fa7774fbaf8ccb93))

# [1.0.0-beta.26](https://github.com/art-framework/art-core/compare/v1.0.0-beta.25...v1.0.0-beta.26) (2020-09-01)


### Bug Fixes

* create configs parent dir and not config as dir ([4be0e68](https://github.com/art-framework/art-core/commit/4be0e6811626278778e2c07cd3811b816637561f))
* **modules:** catch all module errors inside downstream methods ([f5d46b4](https://github.com/art-framework/art-core/commit/f5d46b4885d147c0f14e7fef79ea86017d41b121))
* add default object target for generic action and requirements ([4d954df](https://github.com/art-framework/art-core/commit/4d954df943f3d5d3997c5867452b18da9032afbf))
* use art object provider to create config instance for mapping fields ([6e3dfb2](https://github.com/art-framework/art-core/commit/6e3dfb22ddd60fbc47d4d2ef2de7ccdbc829bbf6))

# [1.0.0-beta.25](https://github.com/art-framework/art-core/compare/v1.0.0-beta.24...v1.0.0-beta.25) (2020-09-01)


### Features

* log registered art and config at registration ([ddce31b](https://github.com/art-framework/art-core/commit/ddce31b42cc122b26de208bea148cc3991144d29))

# [1.0.0-beta.24](https://github.com/art-framework/art-core/compare/v1.0.0-beta.23...v1.0.0-beta.24) (2020-08-31)


### Bug Fixes

* reload configs from disk when module is reloaded ([8974391](https://github.com/art-framework/art-core/commit/89743914db2bc93f78d25a1d6a6c403a87afee96))


### Features

* create and load the config from the module directory ([461f6fd](https://github.com/art-framework/art-core/commit/461f6fd620406c48a9fc43ff12fe4eed98ce2a57))

# [1.0.0-beta.23](https://github.com/art-framework/art-core/compare/v1.0.0-beta.22...v1.0.0-beta.23) (2020-08-30)


### Features

* add autoRegister option to @ART annotation ([e5f5911](https://github.com/art-framework/art-core/commit/e5f591136320a814623f5d8057c2208a70b4239d))

# [1.0.0-beta.22](https://github.com/art-framework/art-core/compare/v1.0.0-beta.21...v1.0.0-beta.22) (2020-08-28)


### Features

* add temporary data storage provider ([7cbbba9](https://github.com/art-framework/art-core/commit/7cbbba92dd0514a73948edef987e182abeaf8f9e))

# [1.0.0-beta.21](https://github.com/art-framework/art-core/compare/v1.0.0-beta.20...v1.0.0-beta.21) (2020-08-28)


### Features

* provide a way for modules to store data ([45743a3](https://github.com/art-framework/art-core/commit/45743a3fce16a2065d09f82b88fba9185b3e0a82))

# [1.0.0-beta.20](https://github.com/art-framework/art-core/compare/v1.0.0-beta.19...v1.0.0-beta.20) (2020-08-28)


### Features

* load all art from modules that are not explicitly tagged with @ArtModule ([5d592be](https://github.com/art-framework/art-core/commit/5d592be0bc1ac7b81a266dac8a86edb9e7c2bacf))

# [1.0.0-beta.19](https://github.com/art-framework/art-core/compare/v1.0.0-beta.18...v1.0.0-beta.19) (2020-08-22)


### Bug Fixes

* make module path sub path of basePath ([3fbb777](https://github.com/art-framework/art-core/commit/3fbb77747c1e8b163272963a3dcd919fa7185fa8))

# [1.0.0-beta.18](https://github.com/art-framework/art-core/compare/v1.0.0-beta.17...v1.0.0-beta.18) (2020-08-22)


### Bug Fixes

* update configuration of scope in bootstrapping phase after each module ([eb72cd2](https://github.com/art-framework/art-core/commit/eb72cd234cc41fe37070b12b8068b1ed75999e3a))

# [1.0.0-beta.17](https://github.com/art-framework/art-core/compare/v1.0.0-beta.16...v1.0.0-beta.17) (2020-08-20)


### Bug Fixes

* create dirs before saving config ([b64beac](https://github.com/art-framework/art-core/commit/b64beac61bd6a7375b1f1524e0779db25c1de932))

# [1.0.0-beta.16](https://github.com/art-framework/art-core/compare/v1.0.0-beta.15...v1.0.0-beta.16) (2020-08-20)


### Bug Fixes

* ignore unknown fields on config deserialization ([a1b43e3](https://github.com/art-framework/art-core/commit/a1b43e3f298ccac0b77ce192211503cb8e56dcf1))


### Code Refactoring

* migrate settings into the Scope ([e57cf17](https://github.com/art-framework/art-core/commit/e57cf17b2e152d27f4e46a6538a5ee5c163579b4))


### BREAKING CHANGES

* settings are no longer inside the configuration and must be accessed directly from the scope

Signed-off-by: Michael Reichenbach <michael@reichenbach.in>

# [1.0.0-beta.15](https://github.com/art-framework/art-core/compare/v1.0.0-beta.14...v1.0.0-beta.15) (2020-08-19)


### Features

* create and inject config fields on module creation ([dadcbb5](https://github.com/art-framework/art-core/commit/dadcbb5b43858b88f309ed0829c1057911f44ef3))


### Reverts

* "refactor: rename @ArtModule to @Module" ([f37b23e](https://github.com/art-framework/art-core/commit/f37b23ee3d7208fa9f9ac3066c767997c672a6e6))

# [1.0.0-beta.14](https://github.com/art-framework/art-core/compare/v1.0.0-beta.13...v1.0.0-beta.14) (2020-08-18)


### Code Refactoring

* rename @ArtModule to @Module ([9dc8494](https://github.com/art-framework/art-core/commit/9dc84947302ad1e8bc989bd67892b8ebe819093e))


### Features

* add fasterxml jackson yaml config provider ([4f5e742](https://github.com/art-framework/art-core/commit/4f5e7424c965914f3045f790e70be593b83d10a3))


### BREAKING CHANGES

* @ArtModule is now called @Module

Signed-off-by: Michael Reichenbach <michael@reichenbach.in>

# [1.0.0-beta.13](https://github.com/art-framework/art-core/compare/v1.0.0-beta.12...v1.0.0-beta.13) (2020-08-18)


### Features

* allow @Config tag on fields ([f339193](https://github.com/art-framework/art-core/commit/f339193127390c3978dc93838b9259983ec06b8a))

# [1.0.0-beta.12](https://github.com/art-framework/art-core/compare/v1.0.0-beta.11...v1.0.0-beta.12) (2020-08-17)


### Bug Fixes

* do not register duplicate trigger ([ae19358](https://github.com/art-framework/art-core/commit/ae19358e09921d855987e251024999b068fb1ec8))

# [1.0.0-beta.11](https://github.com/art-framework/art-core/compare/v1.0.0-beta.10...v1.0.0-beta.11) (2020-08-17)


### Features

* **modules:** create bootstrap lifecycle for modules ([5767233](https://github.com/art-framework/art-core/commit/57672339c9ee56166da1d980d1cd767e7ae5a309))
* split bootstrapping from primary scope ([9040b2a](https://github.com/art-framework/art-core/commit/9040b2a0152134dddf5e23bc36f84f3214ca7cd1))

# [1.0.0-beta.10](https://github.com/art-framework/art-core/compare/v1.0.0-beta.9...v1.0.0-beta.10) (2020-08-15)


### Bug Fixes

* targets extending AbstractTarget are not detected ([32f3ba4](https://github.com/art-framework/art-core/commit/32f3ba437144814914c1ef4c3fd283b7cf889373))


### Code Refactoring

* make Configuration immutable and only creatable by a builder ([c2a8686](https://github.com/art-framework/art-core/commit/c2a86863345a146d47234cd5d787f93bff19862f))


### Features

* implement dynamic module loading system ([119b68e](https://github.com/art-framework/art-core/commit/119b68eb8582271581269799e89ec5b29daba6ca))
* **modules:** add bootstrap lifecycle method ([f931936](https://github.com/art-framework/art-core/commit/f931936b5b767325945b8bb3e04bc2a5e3c3ae13))
* refactor Configuration to live in a Scope ([8aa171b](https://github.com/art-framework/art-core/commit/8aa171b2f619fe6efd3da229698556f27b2b2597))


### BREAKING CHANGES

* every art class now takes a scope instead of a configuration. The scope is now the main entry point into the art-framework.

Signed-off-by: Michael Reichenbach <michael@reichenbach.in>
* the Configuration, Settings and ArtSettings can now only be created with their respective builder.

# [1.0.0-beta.9](https://github.com/art-framework/art-core/compare/v1.0.0-beta.8...v1.0.0-beta.9) (2020-08-11)


### Code Refactoring

* make all methods fluent ([bcff317](https://github.com/art-framework/art-core/commit/bcff3176189eeb3412d73e0068761eb804fde098))


### BREAKING CHANGES

* refactored and cleaned up a lot of non fluent methods making them fluent.

Signed-off-by: Michael Reichenbach <michael@reichenbach.in>

# [1.0.0-beta.8](https://github.com/art-framework/art-core/compare/v1.0.0-beta.7...v1.0.0-beta.8) (2020-08-11)


### Bug Fixes

* **modules:** create new instance of module class on register ([a645266](https://github.com/art-framework/art-core/commit/a645266c5a0595a653369904f45ab5222dcdc692))


### Features

* add ModuleFinder ([aae0e0b](https://github.com/art-framework/art-core/commit/aae0e0b427780fe92f948b173fb53fe5278f9f7a))
* **modules:** add option to provide package names to scan for art ([b49d460](https://github.com/art-framework/art-core/commit/b49d46098142e3db8908ba24266a42f05f8e0421))
* add convenience load(...) method to the Configuration ([a08bb35](https://github.com/art-framework/art-core/commit/a08bb35a49958c684623935710b1fe881c0c42b7))
* add remove methods to the FactoryProvider ([55b2ffb](https://github.com/art-framework/art-core/commit/55b2ffb96cf7d3f32c51babe1ab739ef9fdaf995))

# [1.0.0-beta.7](https://github.com/art-framework/art-core/compare/v1.0.0-beta.6...v1.0.0-beta.7) (2020-08-10)


### Bug Fixes

* **trigger:** provide result creation methods ([29aeab3](https://github.com/art-framework/art-core/commit/29aeab315db7eea3fb69ffbd96423dbbe43f820a))


### Features

* register modules with annotations only ([c9d5573](https://github.com/art-framework/art-core/commit/c9d5573e35b75959dba748aff59c3a27e868c06f))


### BREAKING CHANGES

* removed the ArtModule interface and replaced it with the @ArtModule, @OnLoad, @OnEnable and @OnDisable annotations.

Signed-off-by: Michael Reichenbach <michael@reichenbach.in>

# [1.0.0-beta.6](https://github.com/art-framework/art-core/compare/v1.0.0-beta.5...v1.0.0-beta.6) (2020-08-10)


### Features

* add the option to enable and disable trigger manually ([c48ea65](https://github.com/art-framework/art-core/commit/c48ea65e20b397bf94df92edaf5059d151b0300f))


### BREAKING CHANGES

* triggers in the art context must now be enabled manually for them to work. The reason for this is unwanted side effects of ART configs that should not contain trigger.

# [1.0.0-beta.5](https://github.com/art-framework/art-core/compare/v1.0.0-beta.4...v1.0.0-beta.5) (2020-08-10)


### Features

* **modules:** any class can now be registered as module ([a9bac73](https://github.com/art-framework/art-core/commit/a9bac7333ba67d875e0c0404c458321aa5ee0521))

# [1.0.0-beta.4](https://github.com/art-framework/art-core/compare/v1.0.0-beta.3...v1.0.0-beta.4) (2020-08-10)


### Bug Fixes

* **release:** update yarn.lock and remove husky ([9ccc838](https://github.com/art-framework/art-core/commit/9ccc83853abd3dcd0569da03b99411481ccbac43))
* only calc module meta equals with identifier and class ([5c91b1f](https://github.com/art-framework/art-core/commit/5c91b1f81e6994da5fd485003f9acd2676040507))


### Features

* add cyclic dependency resolver and module resolution ([33d4270](https://github.com/art-framework/art-core/commit/33d4270a66fb525b115c670bcad54cd274fbf3c7))
* add finder provider and art object finder ([2441f70](https://github.com/art-framework/art-core/commit/2441f707345b4425e7d1495df3d555ab5faf8611))
* add module interface and annotations ([bb79800](https://github.com/art-framework/art-core/commit/bb79800e4d9f07bebfe42194c92e287429718dc1))
* add module interface and annotations ([b1e995f](https://github.com/art-framework/art-core/commit/b1e995fd95e2943414a1947f99f54e86165f57a1))
* add target and artobject finder ([5200954](https://github.com/art-framework/art-core/commit/5200954902fd987e3204cd365e554cfeb2f69ab0))
* add the option to use a custom module resolver ([deb99a7](https://github.com/art-framework/art-core/commit/deb99a7a38e3b5eeb213aa74bcf3b4abe89c65a8))
* allow adding targets to the current execution context ([ef9c4a8](https://github.com/art-framework/art-core/commit/ef9c4a82b5440787c36e139aaabea63437f73875))

# [1.0.0-beta.3](https://github.com/art-framework/art-core/compare/v1.0.0-beta.2...v1.0.0-beta.3) (2020-08-03)


### Features

* add the option to provide a trigger with a provider ([a491708](https://github.com/art-framework/art-core/commit/a49170877d782d7ead7c1cc0b044c89afb8c6fe6))

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
* **requirements:** add check_once, count and negated artObjectMeta ([f5cf864](https://github.com/art-framework/art-core/commit/f5cf86434fb47d6f2da2b1a5e5dbb2bce1e52678))
* **requirements:** add check_once, count and negated artObjectMeta ([61b47f9](https://github.com/art-framework/art-core/commit/61b47f9cc6f803536445d04286092a84a259d8df))
* **storageProvider:** add storageProvider api with default memory provider ([79e0fa9](https://github.com/art-framework/art-core/commit/79e0fa999c338567d8e68de589e77fe5a4cb054d)), closes [#7](https://github.com/art-framework/art-core/issues/7)
* **storageProvider:** add storageProvider api with default memory provider ([4999de9](https://github.com/art-framework/art-core/commit/4999de96f7cc4ec678a0e4db620018ef81506782)), closes [#7](https://github.com/art-framework/art-core/issues/7)
* **trigger:** add cooldown and execute_once to trigger ([96bc6e6](https://github.com/art-framework/art-core/commit/96bc6e6703edc3733584d52f18a6226b94ace14b))
* **trigger:** add cooldown and execute_once to trigger ([9f49c50](https://github.com/art-framework/art-core/commit/9f49c501c2c467472fe239de11b0815b495f3397))
* **trigger:** add artObjectMeta to prevent action execution on trigger ([d79c366](https://github.com/art-framework/art-core/commit/d79c3667997d6c0b4aa00229f82cebc9348d2e9d))
* **trigger:** add artObjectMeta to prevent action execution on trigger ([6534f12](https://github.com/art-framework/art-core/commit/6534f12d22f2c6b42ff26a4685fa3f209b528356))
* **trigger:** add artObjectMeta to prevent action execution on trigger ([144b043](https://github.com/art-framework/art-core/commit/144b0431b24c0f3ad5cbbd042397a756d617fa10))
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
* **requirements:** add check_once, count and negated artObjectMeta ([61b47f9](https://github.com/art-framework/art-framework/commit/61b47f9cc6f803536445d04286092a84a259d8df))
* **storageProvider:** add storageProvider api with default memory provider ([4999de9](https://github.com/art-framework/art-framework/commit/4999de96f7cc4ec678a0e4db620018ef81506782)), closes [#7](https://github.com/art-framework/art-framework/issues/7)
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
* **trigger:** add artObjectMeta to prevent action execution on trigger ([144b043](https://github.com/art-framework/art-framework/commit/144b0431b24c0f3ad5cbbd042397a756d617fa10))

# [1.0.0-alpha.8](https://github.com/art-framework/art-framework/compare/v1.0.0-alpha.7...v1.0.0-alpha.8) (2020-07-06)


### Features

* **trigger:** add artObjectMeta to prevent action execution on trigger ([d79c366](https://github.com/art-framework/art-framework/commit/d79c3667997d6c0b4aa00229f82cebc9348d2e9d))
* **trigger:** add artObjectMeta to prevent action execution on trigger ([6534f12](https://github.com/art-framework/art-framework/commit/6534f12d22f2c6b42ff26a4685fa3f209b528356))

# [1.0.0-alpha.7](https://github.com/art-framework/art-framework/compare/v1.0.0-alpha.6...v1.0.0-alpha.7) (2020-07-06)


### Features

* **action:** add execute_once option to actions ([203f7d0](https://github.com/art-framework/art-framework/commit/203f7d0c727dd993acdf16d2c086f567a662cb92))
* **actions:** actions now can have a cooldown ([010fc83](https://github.com/art-framework/art-framework/commit/010fc837e0186dcd5d02ddbd7aa0e54abc9a2ff1)), closes [#7](https://github.com/art-framework/art-framework/issues/7)
* **config:** add BukkitArtConfig to load from a ConfigurationSection ([3baac1d](https://github.com/art-framework/art-framework/commit/3baac1d6400733b7ce9bb373632329a3e667e349))
* **requirements:** add check_once, count and negated artObjectMeta ([f5cf864](https://github.com/art-framework/art-framework/commit/f5cf86434fb47d6f2da2b1a5e5dbb2bce1e52678))
* **storageProvider:** add storageProvider api with default memory provider ([79e0fa9](https://github.com/art-framework/art-framework/commit/79e0fa999c338567d8e68de589e77fe5a4cb054d)), closes [#7](https://github.com/art-framework/art-framework/issues/7)
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

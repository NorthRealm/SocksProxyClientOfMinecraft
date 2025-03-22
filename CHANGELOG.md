# Changelog

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [10.3.0-alpha+1.21.3]

### Added

- Add General category config "httpTestSubjects" for proxy chain testing.
- Add General category config "minecraftTestSubjects" for proxy chain testing.
- Selectively determine whether connection to a specific server should be applied with proxy or not. New button on AddServer screen and DirectConnect screen. New hover text over ping bar icon.
- In General category config screen, "useProxy" option will show how many multiplayer servers will not be applied proxy chain to.
- New translate keys.

### Changed

- Mod metadata
- Translation content
- Internal refactor

### Removed

- Server category config "imposeProxyOnMinecraftLoopback" is no longer available.
- Legacy ping won't work anymore.

## [10.2.0-alpha+1.21.3]

### Added

- New config option for proxying communication with Minecraft Realms API.
- New translate keys.

### Changed

- Proxy testing will ping Minecraft servers.
- Mod metadata.
- Refactored mod update checker.
- Internal refactor.

## [10.1.1-alpha+1.21.3]

### Added

- Mod update checker.

### Changed

- Port to Minecraft 1.21.2, 1.21.3
- Use FabricLoader no lower than 0.16.7

## [10.1.0-alpha+1.21.1]

### Added

- Proxy chaining (#1).
- Button of testing proxy connectivity.

### Changed

- Use FabricLoader no lower than 0.15.11
- Use [YACL](https://modrinth.com/mod/yacl/) instead of Cloth Config.
- Split config into 3 categories.
- Translate keys.
- Internal refactor.

## [10.0.0-alpha+1.21.1]

### Changed

- Use Minecraft 1.21.1 Fabric Yarn mappings.
- Bump version number.
- Internal patch.

## [0.3.1-alpha+1.21]

### Changed

- Port to Minecraft 1.21
- Explicitly declare dependency on [FabricLoader](https://github.com/FabricMC/fabric-loader/releases/tag/0.15.0) no lower than 0.15.0.
- Logger names.
- Internally refactored.

### Fixed

- Fixed missed out part of proxying legacy pinging requests.

## [0.3.0-alpha+1.20.6]

### Changed

- Port to Minecraft 1.20.5 - 1.20.6
- Patch in ClientConnectionMixin and AllowedAddressResolverMixin

## [0.2.1-alpha+1.20.4]

### Changed

- Port to Minecraft 1.20.3 - 1.20.4
- Minor patch.

## [0.2.0-alpha+1.20.2]

### Changed

- Port to Minecraft 1.20.2

### Removed

- The mod will do nothing should game client be in offline mode at launch.
- Removed "Recreate auth service" button, and all its corresponding functions.

## [0.0.3-alpha+1.20.1]

### Added

- Added new config option "Dismiss system hosts file"("minecraftRemoteResolveDismissSystemHosts") in "Server settings" section

### Changed

- Compatability with [Fast IP Ping](https://modrinth.com/mod/fast-ip-ping)

## [0.0.2-alpha+1.20.1]

### Added

- Let communication with Realms going through proxy as much as possible.

### Changed

- Renamed config option "Use proxy on Yggdrasil authentication service"("proxyYggdrasilAuth")
  to "Use proxy on Yggdrasil service interaction"("proxyYggdrasil") in "Server settings" section.
- Made patches to Mixins fetching or setting Proxy object properties.
- If game client is in offline mode, the mod will try to recreate auth services once.
  (reopen communication with Yggdrasil, and other essential services engaged in joining servers.)

### Fixed

- Fixed missed out parts of proxying on Yggdrasil.

## [0.0.1-alpha+1.20.1] - 2024-06-03

### Added

- SOCKS proxy support. Explicitly proxy Minecraft server traffic.
- Convert SOCKS proxy to HTTP(S) proxy. Proxy communications with Mojang Yggdrasil auth service, player skin download endpoint, and server resource pack downloading.
- Remotely resolve Minecraft server's domain name through SOCKS proxy with the help of [DNS over HTTPS (DoH)](https://www.rfc-editor.org/rfc/rfc8484)

[Unreleased]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v10.3.0-alpha+1.21.3...HEAD
[10.3.0-alpha+1.21.3]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v10.2.0-alpha+1.21.3...v10.3.0-alpha+1.21.3
[10.2.0-alpha+1.21.3]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v10.1.1-alpha+1.21.3...v10.2.0-alpha+1.21.3
[10.1.1-alpha+1.21.3]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v10.1.0-alpha+1.21.1...v10.1.1-alpha+1.21.3
[10.1.0-alpha+1.21.1]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v10.0.0-alpha+1.21.1...v10.1.0-alpha+1.21.1
[10.0.0-alpha+1.21.1]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v0.3.1-alpha+1.21...v10.0.0-alpha+1.21.1
[0.3.1-alpha+1.21]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v0.3.0-alpha+1.20.6...v0.3.1-alpha+1.21
[0.3.0-alpha+1.20.6]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v0.2.1-alpha+1.20.4...v0.3.0-alpha+1.20.6
[0.2.1-alpha+1.20.4]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v0.2.0-alpha+1.20.2...v0.2.1-alpha+1.20.4
[0.2.0-alpha+1.20.2]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v0.0.3-alpha+1.20.1...v0.2.0-alpha+1.20.2
[0.0.3-alpha+1.20.1]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v0.0.2-alpha+1.20.1...v0.0.3-alpha+1.20.1
[0.0.2-alpha+1.20.1]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/compare/v0.0.1-alpha+1.20.1...v0.0.2-alpha+1.20.1
[0.0.1-alpha+1.20.1]: https://github.com/NorthRealm/SocksProxyClientOfMinecraft/commits/v0.0.1-alpha+1.20.1

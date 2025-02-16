# Changelog for Enemy Echelons 1.20.1

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.4.0]  - 2025-02-13

### Added
- config option to enable/disable custom HUD range. using a custom HUD range is more computationally expensive on the client side.

### Changed
- added condition to check for the Custom HUD range config. If =false, then use vanilla.


## [1.3.0] - 2024-09-07

### Changed
- moved hudRange config option to Server-side. Can give unfair advantages.
- fixed mob level determination when mobs are blacklisted from all echelons. 
- fixed update url to point to the correct file.
- reworked internal storage and references to echelons and histograms.
  -- works for a wider range of echelon configurations now.
- fixed pack.mcmeta
- changed isValidEntity() to check against Mob instead of Enemy.

## [1.2.0] - 2024-01-31

### Changed

- Port from 1.19.3
- Using Changelod instead of update.json.
- Reduced default HUD range to 10.
- Updated echelons toml file to v2.
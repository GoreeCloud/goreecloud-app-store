# Release-channel rollout

This development checkpoint adds the authorization vocabulary and tests required for account-dependent Stable, RC, Beta, Development, and Debug access.

The next implementation layer is catalog/artifact metadata capable of representing multiple artifacts for one product, each with its own channel, version, package identity, provenance, signing evidence, and download authorization. The UI should only present channels returned by `ReleaseChannelAccess.visibleChannels(session)` and must not infer access from administrator or developer labels alone.

No production download/install behavior is enabled by this checkpoint.

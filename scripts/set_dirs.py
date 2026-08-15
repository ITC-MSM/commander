"""Shared mapping between `definitions/<dir>` directories and set codes.

The directory name under `definitions/` usually equals the lowercase set code, but can't
always: `con` is a reserved filename on Windows (DOS device name), so Conflux lives in
`definitions/conflux/`. The authoritative code is the `override val code = "..."` declaration
in each directory's `*Set.kt` — scripts must read that instead of trusting the directory name.

Card definitions are spread across several Gradle modules under `mtg-sets/` (`mtg-sets/core` for
the setless `custom/` cards, then one `mtg-sets/<era>` module per fixed release-year range) so that
no single Kotlin compilation has to hold the whole corpus. There is therefore no *one* definitions
root any more — use [definitions_roots], [iter_set_dirs] or [iter_card_files] instead of building a
path by hand, and they keep working when a new era module is appended.
"""

from __future__ import annotations

import re
from pathlib import Path
from typing import Iterator

REPO_ROOT = Path(__file__).resolve().parent.parent

_DEFINITIONS_SUFFIX = "src/main/kotlin/com/wingedsheep/mtg/sets/definitions"

SET_CODE_RE = re.compile(r'override\s+val\s+code\s*=\s*"([^"]+)"')


def _module_order(module: Path) -> tuple[int, str]:
    """`mtg-sets/core` first (setless cards), then the era modules oldest to newest.

    Plain alphabetical sorting would put `core` after `2026`, which matters because [root_for_set]
    treats the last entry as "where a brand new set goes".
    """
    return (0, "") if module.name == "core" else (1, module.name)


def definitions_roots() -> list[Path]:
    """Every module-level `definitions/` directory: core first, then era modules oldest to newest."""
    return [
        module / _DEFINITIONS_SUFFIX
        for module in sorted((REPO_ROOT / "mtg-sets").iterdir(), key=_module_order)
        if (module / _DEFINITIONS_SUFFIX).is_dir()
    ]


def iter_set_dirs() -> Iterator[Path]:
    """Every `definitions/<set>` directory across all card modules."""
    for root in definitions_roots():
        for d in sorted(root.iterdir()):
            if d.is_dir():
                yield d


def iter_card_files() -> Iterator[Path]:
    """Every `definitions/<set>/cards/*.kt` across all card modules."""
    for root in definitions_roots():
        yield from sorted(root.glob("*/cards/*.kt"))


def set_dir_paths() -> dict[str, Path]:
    """Map each `definitions/<dir>` name to its absolute path."""
    return {d.name: d for d in iter_set_dirs()}


def root_for_set(dir_name: str) -> Path:
    """The `definitions/` root that owns (or should own) `dir_name`.

    Existing sets resolve to the module they already live in. An unknown set is placed in the
    newest era module, which is where a set being scaffolded today belongs; move it if the set is
    an older release.
    """
    existing = set_dir_paths().get(dir_name)
    if existing is not None:
        return existing.parent
    return definitions_roots()[-1]


def set_dir_codes() -> dict[str, str]:
    """Map each definitions/<dir> name to its lowercase set code."""
    codes: dict[str, str] = {}
    for d in iter_set_dirs():
        code = None
        for set_kt in sorted(d.glob("*Set.kt")):
            m = SET_CODE_RE.search(set_kt.read_text(encoding="utf-8"))
            if m:
                code = m.group(1).lower()
                break
        codes[d.name] = code or d.name
    return codes


def dir_for_codes() -> dict[str, str]:
    """Reverse map: lowercase set code -> definitions/<dir> name."""
    return {code: d for d, code in set_dir_codes().items()}


def scaffolded_set_codes() -> set[str]:
    """Lowercase set codes of every scaffolded definitions/<dir>."""
    return set(set_dir_codes().values())

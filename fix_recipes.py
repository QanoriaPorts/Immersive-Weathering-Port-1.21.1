#!/usr/bin/env python3
"""
Migrate IW recipe JSONs from the 1.20.x ingredient string shorthand to the
1.21.x explicit Ingredient object form.

Affects:
  - Shaped recipes ("key" map)             : value strings -> {"item": ...} or {"tag": ...}
  - Stonecutting / smelting / smoking /
    blasting / campfire_cooking recipes    : "ingredient" string -> object
  - Shapeless recipes ("ingredients" list) : entry strings -> objects

Strings starting with '#' are tag references; everything else is treated as
an item id. Already-wrapped objects are left alone. Arrays of ingredients
are recursed into.

Run from the project root:
    python fix_recipes.py
"""
from __future__ import annotations
import json
import sys
from pathlib import Path
from collections import Counter


def wrap_ingredient(node):
    """Recursively wrap any ingredient string into the 1.21 object form."""
    if isinstance(node, str):
        if node.startswith("#"):
            return {"tag": node[1:]}
        else:
            return {"item": node}
    if isinstance(node, list):
        return [wrap_ingredient(x) for x in node]
    return node  # already an object/dict — leave it


def fix_recipe(data: dict) -> tuple[dict, bool]:
    """Return (possibly-modified copy, did_we_change_anything)."""
    changed = False

    # Shaped recipes: every value under "key"
    if "key" in data and isinstance(data["key"], dict):
        for k, v in list(data["key"].items()):
            new_v = wrap_ingredient(v)
            if new_v is not v and new_v != v:
                data["key"][k] = new_v
                changed = True

    # Stonecutting / smelting / smoking / blasting / campfire_cooking
    if "ingredient" in data:
        new_v = wrap_ingredient(data["ingredient"])
        if new_v != data["ingredient"]:
            data["ingredient"] = new_v
            changed = True

    # Shapeless recipes: every entry under "ingredients"
    if "ingredients" in data and isinstance(data["ingredients"], list):
        new_list = [wrap_ingredient(x) for x in data["ingredients"]]
        if new_list != data["ingredients"]:
            data["ingredients"] = new_list
            changed = True

    return data, changed


def main() -> int:
    root = Path("common/src/main/resources/data/immersive_weathering/recipe")
    if not root.exists():
        print(f"Recipe root not found: {root.resolve()}", file=sys.stderr)
        return 1

    counts = Counter()
    for path in sorted(root.rglob("*.json")):
        try:
            text = path.read_text(encoding="utf-8")
            data = json.loads(text)
        except Exception as e:
            counts["read_error"] += 1
            print(f"  [skip] {path}: {e}", file=sys.stderr)
            continue

        if not isinstance(data, dict):
            counts["non_object"] += 1
            continue

        new_data, changed = fix_recipe(data)
        if changed:
            path.write_text(
                json.dumps(new_data, indent=2, ensure_ascii=False) + "\n",
                encoding="utf-8",
            )
            counts["updated"] += 1
        else:
            counts["unchanged"] += 1

    print(f"\nDone. {dict(counts)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

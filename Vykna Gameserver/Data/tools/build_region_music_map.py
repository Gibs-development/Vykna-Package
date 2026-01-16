#!/usr/bin/env python3
"""
build_region_music_map.py

Takes:
  1) File 1: a text file containing entries like:
     {"Harmony", "harmony", 12850},
     {"Attack 4", "attack4", 10289, 10389},
  where the numbers are REGION IDs (x>>6<<8 | y>>6)

  2) File 2: JSON mapping songId -> songName:
     { "songs": { "76": "harmony", ... } }

Outputs:
  Data/music/region_id_to_song_id.json:
     { "regions": { "12850": 76, "10289": 27, ... } }

Also prints:
  - songs in File 1 that couldn't be matched to a songId in File 2
  - duplicate region assignments (if any)

Run:
  python tools/build_region_music_map.py --file1 Data/music/file1_triggers.txt --file2 Data/music/song_id_to_name.json --out Data/music/region_id_to_song_id.json
"""
import argparse, json, re, sys
from collections import defaultdict

def norm(s: str) -> str:
    s = s.lower()
    return "".join(ch for ch in s if ch.isalnum())

ENTRY_RE = re.compile(r'\{\s*"([^"]+)"\s*,\s*"([^"]+)"\s*(?:,\s*([^}]+?))?\s*\}\s*,?')

def parse_file1(text: str):
    out = []
    for m in ENTRY_RE.finditer(text):
        title = m.group(1)
        slug = m.group(2)
        tail = m.group(3) or ""
        # Extract ints in the tail (region IDs)
        ids = [int(x) for x in re.findall(r'\b\d+\b', tail)]
        out.append((title, slug, ids))
    return out

def load_file2(path: str):
    with open(path, "r", encoding="utf-8") as f:
        root = json.load(f)
    songs = root.get("songs", {})
    # Build normalized name -> songId
    name_to_id = {}
    for sid, name in songs.items():
        try:
            sid_int = int(sid)
        except:
            continue
        if not isinstance(name, str):
            continue
        name_to_id[norm(name)] = sid_int
    return name_to_id

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--file1", required=True)
    ap.add_argument("--file2", required=True)
    ap.add_argument("--out", required=True)
    args = ap.parse_args()

    with open(args.file1, "r", encoding="utf-8") as f:
        file1_text = f.read()

    entries = parse_file1(file1_text)
    name_to_id = load_file2(args.file2)

    region_to_song = {}
    missing = []
    dup_regions = defaultdict(list)

    for title, slug, region_ids in entries:
        if not region_ids:
            continue
        # Try match slug first (best), then title
        song_id = name_to_id.get(norm(slug)) or name_to_id.get(norm(title))
        if song_id is None:
            missing.append((title, slug, region_ids[:]))
            continue
        for rid in region_ids:
            if rid in region_to_song and region_to_song[rid] != song_id:
                dup_regions[rid].append((region_to_song[rid], song_id, title))
            region_to_song[rid] = song_id

    out = {"regions": {str(k): v for k, v in sorted(region_to_song.items())}}

    with open(args.out, "w", encoding="utf-8") as f:
        json.dump(out, f, indent=2)

    print(f"Wrote {len(region_to_song)} region->song mappings to {args.out}")

    if missing:
        print("\nMissing songs (present in File1 but not found in File2):")
        for title, slug, ids in missing[:50]:
            print(f"  - {title} / {slug}  regions={ids[:10]}{'...' if len(ids)>10 else ''}")
        if len(missing) > 50:
            print(f"  ... and {len(missing)-50} more")

    if dup_regions:
        print("\nDuplicate region assignments (same region listed under multiple tracks):")
        for rid, infos in list(dup_regions.items())[:30]:
            print(f"  region {rid}: {infos}")
        if len(dup_regions) > 30:
            print(f"  ... and {len(dup_regions)-30} more")

if __name__ == "__main__":
    main()

#!/usr/bin/env python3
"""Ensure first top-level type in OA api/biz Java files has Javadoc with @author and @date."""
import re
import sys
from pathlib import Path

AUTHOR = "qinlei"
DATE = "2026/4/5"


def first_top_level_type_line(lines: list[str]) -> int | None:
	for i, line in enumerate(lines):
		if line.startswith("public ") and re.search(
			r"\b(class|interface|enum|record)\b", line
		):
			return i
	return None


def annotation_block_start(lines: list[str], type_idx: int) -> int:
	"""First line index of contiguous @annotations directly above the type declaration."""
	i = type_idx - 1
	while i >= 0 and lines[i].strip() == "":
		i -= 1
	start = type_idx
	while i >= 0 and lines[i].lstrip().startswith("@"):
		start = i
		i -= 1
		while i >= 0 and lines[i].strip() == "":
			i -= 1
	return start


def javadoc_range_before(lines: list[str], type_idx: int) -> tuple[int, int] | None:
	"""If there is a Javadoc block ending immediately before annotations/type, return (start, end) inclusive."""
	i = type_idx - 1
	while i >= 0 and lines[i].strip() == "":
		i -= 1
	if i < 0:
		return None
	if lines[i].strip() != "*/":
		return None
	end = i
	i -= 1
	while i >= 0:
		if lines[i].strip().startswith("/**"):
			return (i, end)
		i -= 1
	return None


def fix_file(path: Path) -> bool:
	raw = path.read_text(encoding="utf-8")
	nl = "\r\n" if "\r\n" in raw else "\n"
	lines = raw.splitlines()

	type_idx = first_top_level_type_line(lines)
	if type_idx is None:
		return False

	jd = javadoc_range_before(lines, annotation_block_start(lines, type_idx))
	if jd is not None:
		js, je = jd
		block = nl.join(lines[js : je + 1])
		has_author = "@author" in block
		has_date = "@date" in block
		if has_author and has_date:
			return False
		closing_idx = je
		prefix = lines[closing_idx][: len(lines[closing_idx]) - len(lines[closing_idx].lstrip())]
		insert: list[str] = []
		if not has_author:
			insert.append(f"{prefix}* @author {AUTHOR}")
		if not has_date:
			insert.append(f"{prefix}* @date {DATE}")
		lines[closing_idx:closing_idx] = insert
		path.write_text(nl.join(lines) + (nl if raw.endswith(nl) or not raw else ""), encoding="utf-8")
		return True

	ann_start = annotation_block_start(lines, type_idx)
	first_line = lines[type_idx].strip()
	type_name = "类型"
	if "class" in first_line:
		m = re.search(r"class\s+(\w+)", first_line)
		if m:
			type_name = m.group(1)
	elif "interface" in first_line:
		m = re.search(r"interface\s+(\w+)", first_line)
		if m:
			type_name = m.group(1)
	elif "enum" in first_line:
		m = re.search(r"enum\s+(\w+)", first_line)
		if m:
			type_name = m.group(1)
	prefix = lines[type_idx][: len(lines[type_idx]) - len(lines[type_idx].lstrip())]
	doc_lines = [
		f"{prefix}/**",
		f"{prefix} * {type_name}。",
		f"{prefix} *",
		f"{prefix} * @author {AUTHOR}",
		f"{prefix} * @date {DATE}",
		f"{prefix} */",
		"",
	]
	lines[ann_start:ann_start] = doc_lines
	ending = nl if raw.endswith(nl) or not raw.strip() else ""
	path.write_text(nl.join(lines) + ending, encoding="utf-8")
	return True


def main() -> None:
	root = Path(__file__).resolve().parents[1]
	changed = 0
	for line in sys.stdin:
		p = (root / line.strip()).resolve()
		if not p.exists():
			print("missing", p, file=sys.stderr)
			continue
		if fix_file(p):
			changed += 1
			print("fixed", p.relative_to(root))
	print("total changed", changed, file=sys.stderr)


if __name__ == "__main__":
	main()

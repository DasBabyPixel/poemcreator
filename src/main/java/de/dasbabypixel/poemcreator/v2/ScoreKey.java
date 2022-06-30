package de.dasbabypixel.poemcreator.v2;

import java.util.Objects;

public class ScoreKey {

	public String l1;
	public String l2;

	public ScoreKey(String l1, String l2) {
		this.l1 = l1;
		this.l2 = l2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(l1, l2) + Objects.hash(l2, l1);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScoreKey other = (ScoreKey) obj;
		return (Objects.equals(l1, other.l1) && Objects.equals(l2, other.l2))
				|| (Objects.equals(l1, other.l2) && Objects.equals(l2, other.l1));
	}

	@Override
	public String toString() {
		return "ScoreKey [l1=" + l1 + ", l2=" + l2 + "]";
	}
}

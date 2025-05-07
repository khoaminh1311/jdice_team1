import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DiceParser {
	/* this is a helper class to manage the input "stream" */
	private static final Logger logger = Logger.getLogger(DiceParser.class.getName());

	private static class StringStream {
		StringBuffer buff;

		public StringStream(String s) {
			buff = new StringBuffer(s);
		}

		private void munchWhiteSpace() {
			int index = 0;
			char curr;
			while (index < buff.length()) {
				curr = buff.charAt(index);
				if (!Character.isWhitespace(curr))
					break;
				index++;
			}
			buff.delete(0, index); //Loại bỏ gán lại không cần thiết
		}

		public boolean isEmpty() {
			munchWhiteSpace();
			// return buff.toString().equals("");
			return buff.isEmpty();
		}

		public Integer getInt() {
			return readInt();
		}

		public Integer readInt() {
			int index = 0;
			char curr;
			munchWhiteSpace();
			while (index < buff.length()) {
				curr = buff.charAt(index);
				if (!Character.isDigit(curr))
					break;
				index++;
			}
			try {
				int ans; // chuyen sang kieu int để đỡ tố bo nhớ và hiệu năng vi bien ans ko can luu gia trị null
				ans = Integer.parseInt(buff.substring(0, index));
				buff.delete(0, index);
				return ans;
			} catch (Exception e) {
				return null;
			}
		}

		public Integer readSgnInt() {
			munchWhiteSpace();
			StringStream state = save();
			if (checkAndEat("+")) {
				Integer ans = readInt();
				if (ans != null){
					return ans;
				}
					
				restore(state);
				return null;
			}
			if (checkAndEat("-")) {
				Integer ans = readInt();
				if (ans != null){
					return -ans;
				}
					
				restore(state);
				return null;
			}
			return readInt();
		}
		/**
		 * Refactored:
		 * - Loại bỏ gán lại không cần thiết: thay `buff = buff.delete(...)` bằng `buff.delete(...)`.
		 * - Vì delete() đã thay đổi nội bộ buffer nên không cần gán lại.
		 * - Giúp giảm thao tác và làm code ngắn gọn, rõ ràng hơn.
		 */
		public boolean checkAndEat(String s) {
			munchWhiteSpace();
			if (buff.indexOf(s) == 0) {
				buff.delete(0, s.length());
				return true;
			}
			return false;
		}

		public StringStream save() {
			return new StringStream(buff.toString());
		}

		public void restore(StringStream ss) {
			this.buff = new StringBuffer(ss.buff);
		}

		public String toString() {
			return buff.toString();
		}

	}

	/**
	 * roll::= ndice ; roll
	 * | ndice
	 * xdice::= dice
	 * | N X dice
	 * dice::= die bonus? dtail
	 * XXXX| FA(die,bonus,N) dtail
	 * dtail::= & dice
	 * | <nothing>
	 * die::= (N)? dN
	 * bonus::= + N
	 * | -N
	 **/

	public static Vector<DieRoll> parseRoll(String s) {
		StringStream ss = new StringStream(s.toLowerCase());
		Vector<DieRoll> v = parseRollInner(ss, new Vector<DieRoll>());
		if (ss.isEmpty()){
			return v;
		}
		return null;
	}

	private static Vector<DieRoll> parseRollInner(StringStream ss, Vector<DieRoll> v) {
		Vector<DieRoll> r = parseXDice(ss);
		if (r == null) {
			return null;
		}
		v.addAll(r);
		if (ss.checkAndEat(";")) {
			return parseRollInner(ss, v);
		}
		return v;
	}

	private static Vector<DieRoll> parseXDice(StringStream ss) {
		StringStream saved = ss.save();
		// Integer x = ssgetInt();
		Integer x = ss.readInt(); //goi trực tiếp hàm readInt() thay vì getInt() vì hàm getInt cũng chỉ gọi ra ham readInt()
		int num;
		if (x == null) {
			num = 1;
		} else {
			if (ss.checkAndEat("x")) {
				num = x;
			} else {
				ss.restore(saved); //loai bo mum = 1: vì restore rồi lại gán num = 1 là không hợp lý
			}
		}
		DieRoll dr = parseDice(ss);
		if (dr == null) {
			return null;
		}
		Vector<DieRoll> ans = new Vector<DieRoll>();
		for (int i = 0; i < num; i++) {
			ans.add(dr);
		}
		return ans;
	}

	/**
	 * dice::= die (bonus?) dtail
	 * XXXX| FA(die,bonus,N) dtail
	 */
	private static DieRoll parseDice(StringStream ss) {
		return parseDTail(parseDiceInner(ss), ss);
	}

	private static DieRoll parseDiceInner(StringStream ss) {
		/*
		 * if(checkAndEat("FA(")) {
		 * DieRoll d=parseFA(ss);
		 * if(d==null)
		 * return null;
		 * return parseDTail(d,ss);
		 * }
		 */
		Integer num = ss.getInt();
		int dsides;
		int ndice;
		if (num == null) {
			ndice = 1;
		} else {
			ndice = num;
		}
		if (ss.checkAndEat("d")) {
			num = ss.getInt();
			if (num == null)
				return null;
			dsides = num;
		} else {
			return null;
		}
		num = ss.readSgnInt();
		int bonus;
		if (num == null)
			bonus = 0;
		else
			bonus = num;
		return new DieRoll(ndice,
				dsides,
				bonus);

	}

	private static DieRoll parseDTail(DieRoll r1,
			StringStream ss) {
		if (r1 == null)
			return null;
		if (ss.checkAndEat("&")) {
			DieRoll d2 = parseDice(ss);
			return parseDTail(new DiceSum(r1,d2),ss); // mở lại comment vì thiếu return
		} else {
			return r1;
		}
	}

	private static void test(String s) {
		Vector<DieRoll> v = parseRoll(s);
		int i;
		if (v == null)
			logger.warning(()-> "Failure: " +s );
		else {
			logger.info(()-> "Results for : " +s+":");
			for (i = 0; i < v.size(); i++) {
				DieRoll dr = v.get(i);
				logger.info(() -> dr.toString() + ": " + dr.makeRoll());
			}
		}
	}

	public static void main(String[] args) {
		test("d6");
		test("2d6");
		test("d6+5");
		test("4X3d8-5");
		test("12d10+5 & 4d6+2");
		test("d6 ; 2d4+3");
		test("4d6+3 ; 8d12 -15 ; 9d10 & 3d6 & 4d12 +17");
		test("4d6 + xyzzy");
		test("hi");
		test("4d4d4");
	}

}

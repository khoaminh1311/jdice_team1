package bainhom;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Lớp RollResult đại diện cho kết quả của một hoặc nhiều lần tung xúc xắc.
 * Lưu trữ tổng điểm, giá trị modifier và danh sách các kết quả từng lần tung.
 * Các thay đổi được bổ sung:
 *   Thay thế Vector bằng ArrayList để cải thiện hiệu suất và chuẩn hiện đại.
 *   Thêm kiểm tra dữ liệu đầu vào: không cho phép giá trị âm khi thêm kết quả tung.
 *   Thêm ghi log bằng java.util.logging để theo dõi hoạt động.</li>
 *   Sử dụng toán tử diamond (&lt;&gt;) để đơn giản hóa khai báo generic.
 *   Chuyển mã nguồn vào package có tên để tránh cảnh báo.
 */
public class RollResult {
    int total;
    int modifier;
    ArrayList<Integer> rolls;

    /** Logger để ghi lại hoạt động và cảnh báo */
    private static final Logger logger = Logger.getLogger(RollResult.class.getName());

    /**
     * Hàm dựng riêng dùng nội bộ, tạo đối tượng RollResult với giá trị tùy chỉnh.
     *
     * @param total    Tổng điểm từ các lần tung
     * @param modifier Giá trị cộng thêm hoặc trừ
     * @param rolls    Danh sách các kết quả tung riêng lẻ
     */
    private RollResult(int total, int modifier, ArrayList<Integer> rolls) {
        this.total = total;
        this.modifier = modifier;
        this.rolls = rolls;
    }

    /**
     * Hàm dựng công khai, khởi tạo với một giá trị bonus.
     * 
     * @param bonus Giá trị khởi tạo cho cả tổng và modifier
     */
    public RollResult(int bonus) {
        this.total = bonus;
        this.modifier = bonus;
        this.rolls = new ArrayList<>();
    }

    /**
     * Thêm kết quả tung xúc xắc vào danh sách kết quả.
     * 
     * <p>
     * ✅ Có kiểm tra dữ liệu: nếu kết quả âm thì không thêm và ghi cảnh báo.
     * </p>
     * 
     * @param res Kết quả tung (phải >= 0)
     */
    public void addResult(int res) {
        if (res < 0) {
            logger.warning(() -> "Không thể thêm kết quả âm: " + res);
            return;
        }
        total += res;
        rolls.add(res);
        logger.info(() -> "Đã thêm kết quả: " + res + " | Tổng mới: " + total);
    }

    /**
     * Gộp kết quả hiện tại với một kết quả khác.
     * 
     * @param r2 Đối tượng {@code RollResult} khác để gộp
     * @return Đối tượng {@code RollResult} mới với giá trị gộp
     */
    public RollResult andThen(RollResult r2) {
        int combinedTotal = this.total + r2.total;
        ArrayList<Integer> combinedRolls = new ArrayList<>();
        combinedRolls.addAll(this.rolls);
        combinedRolls.addAll(r2.rolls);
        return new RollResult(combinedTotal, this.modifier + r2.modifier, combinedRolls);
    }

    /**
     * Trả về chuỗi biểu diễn kết quả tung xúc xắc.
     * 
     * @return Chuỗi dạng: "tổng <= [các lần tung] +modifier"
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(total).append("  <= ").append(rolls.toString());
        if (modifier > 0) {
            sb.append("+").append(modifier);
        } else if (modifier < 0) {
            sb.append(modifier);
        }
        return sb.toString();
    }
}

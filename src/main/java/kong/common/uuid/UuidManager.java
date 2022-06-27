package kong.common.uuid;

public class UuidManager {

    // UUID 값을 인덱싱에 유리하도록 구조 변경
    public static String toIndexingUuid(String uuid) {
        String[] uuidTokens = uuid.split("-");
        return uuidTokens[2] + uuidTokens[1] + uuidTokens[0] + uuidTokens[3] + uuidTokens[4];
    }

    // 변경된 UUID를 원래 형태로 변경
    public static String toUuid(String indexingUuid) {
        return indexingUuid.substring(8, 16) + "-" + indexingUuid.substring(4, 8) + "-" + indexingUuid.substring(0, 4) + "-" +
                indexingUuid.substring(16, 20) + "-" + indexingUuid.substring(20);
    }
}

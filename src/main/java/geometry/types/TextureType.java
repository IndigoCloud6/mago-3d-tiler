package geometry.types;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum TextureType {
    NONE((byte) 0),
    DIFFUSE((byte) 1),
    SPECULAR((byte) 2),
    AMBIENT((byte) 3),
    EMISSIVE((byte) 4),
    HEIGHT((byte) 5),
    NORMALS((byte) 6),
    SHININESS((byte) 7),
    OPACITY((byte) 8),
    DISPLACEMENT((byte) 9),
    LIGHTMAP((byte) 10),
    REFLECTION((byte) 11),
    BASE_COLOR((byte) 12),
    NORMAL_CAMERA((byte) 13),
    EMISSION_COLOR((byte) 14),
    METALNESS((byte) 15),
    DIFFUSE_ROUGHNESS((byte) 16),
    AMBIENT_OCCLUSION((byte) 17),
    UNKNOWN((byte) 18);

    private byte value;
    TextureType(byte value) {
        this.value = value;
    }

    public static TextureType fromValue(byte value) {
        for (TextureType type : TextureType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return TextureType.UNKNOWN;
    }
}

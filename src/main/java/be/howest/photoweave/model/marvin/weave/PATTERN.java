package be.howest.photoweave.model.marvin.weave;

public class PATTERN {
    private static Integer WHITE = -1;
    private static Integer BLACK = -16777216;

    public static Integer[][] ONE =
            {
                    {BLACK, BLACK, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, BLACK, BLACK},
                    {BLACK, BLACK, BLACK, BLACK, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE},
                    {BLACK, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, BLACK, BLACK, BLACK},
                    {BLACK, BLACK, BLACK, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, BLACK},
                    {WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, BLACK, BLACK, BLACK, BLACK},
                    {BLACK, BLACK, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, BLACK, BLACK},
                    {BLACK, BLACK, BLACK, BLACK, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE},
                    {BLACK, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, BLACK, BLACK, BLACK},
                    {BLACK, BLACK, BLACK, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, BLACK},
                    {WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, BLACK, BLACK, BLACK, BLACK},
            };
    public static Integer[][] TWO = {{}};
    public static Integer[][] THREE = {{}};
    public static Integer[][] FOUR = {
            {BLACK, BLACK, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE},
            {WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE},
            {BLACK, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, BLACK},
            {WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE},
            {WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, BLACK, BLACK},
            {BLACK, BLACK,WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE},
            {WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE},
            {BLACK, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, BLACK},
            {WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE},
            {WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, BLACK, BLACK},

    };
    public static Integer[][] FIVE = {{}};
    public static Integer[][] SIX = {{}};
    public static Integer[][] SEVEN = {{}};
    public static Integer[][] EIGHT = {{}};
    public static Integer[][] NINE = {{}};
    public static Integer[][] TEN = {{}};
    public static Integer[][] ELEVEN = {{}};
    public static Integer[][] TWELVE = {{}};
    public static Integer[][] THIRDTEEN = {{}};
    public static Integer[][] FOURTEEN = {{}};
    public static Integer[][] FIFTEEN = {{}};
    public static Integer[][] SIXTEEN = {{}};


    private static Integer[][] FULL_BLACK = {
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
    };
    private static Integer[][] FULL_WHITE = {
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
    };


}

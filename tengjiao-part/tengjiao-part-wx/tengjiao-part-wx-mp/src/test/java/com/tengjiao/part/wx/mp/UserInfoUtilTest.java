package com.tengjiao.part.wx.mp;

/**
 * @author tengjiao
 * @description
 * @date 2021/11/21 0:23
 */
public class UserInfoUtilTest {

    public static void testGetUserInfo() {

        String encryptedData =
          "CiyLU1Aw2KjvrjMdj8YKliAjtP4gsMZM"+
          "QmRzooG2xrDcvSnxIMXFufNstNGTyaGS"+
          "9uT5geRa0W4oTOb1WT7fJlAC+oNPdbB+"+
          "3hVbJSRgv+4lGOETKUQz6OYStslQ142d"+
          "NCuabNPGBzlooOmB231qMM85d2/fV6Ch"+
          "evvXvQP8Hkue1poOFtnEtpyxVLW1zAo6"+
          "/1Xx1COxFvrc2d7UL/lmHInNlxuacJXw"+
          "u0fjpXfz/YqYzBIBzD6WUfTIF9GRHpOn"+
          "/Hz7saL8xz+W//FRAUid1OksQaQx4CMs"+
          "8LOddcQhULW4ucetDf96JcR3g0gfRK4P"+
          "C7E/r7Z6xNrXd2UIeorGj5Ef7b1pJAYB"+
          "6Y5anaHqZ9J6nKEBvB4DnNLIVWSgARns"+
          "/8wR2SiRS7MNACwTyrGvt9ts8p12PKFd"+
          "lqYTopNHR1Vf7XjfhQlVsAJdNiKdYmYV"+
          "oKlaRv85IfVunYzO0IKXsyl7JCUjCpoG"+
          "20f0a04COwfneQAGGwd5oa+T8yO5hzuy"+
          "Db/XcxxmK01EpqOyuxINew=="
          ;

        String sessionKey = "tiihtNczf5v6AKRyjwEUhQ=="
          ;

        String iv = "r7BXXKkLb8qrSNn05n0qiA=="
          ;

        final String orig = UserInfoUtil.getUserInfo(encryptedData, sessionKey, iv);

        System.out.println(orig);
    }

    public static void main(String[] args) {

        testGetUserInfo();

    }

}

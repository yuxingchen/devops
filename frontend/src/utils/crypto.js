import CryptoJS from 'crypto-js';

const aesKey = '4P6ZAoKammX6IhZUd+w8vw==';

/**
 * AES加密
 * @param content 内容
 * @return {string} Base64加密后的内容
 */
export function encryptBase64(content) {
    const key = CryptoJS.enc.Base64.parse(aesKey);
    const encrypt = CryptoJS.AES.encrypt(content, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
    return CryptoJS.enc.Base64.stringify(encrypt.ciphertext);
}

/**
 * AES解密
 * @param content 内容
 * @return {string} Base64解密密后的内容
 */
export function decryptBase64(content) {
    const decrypt = CryptoJS.AES.decrypt({ciphertext: content}, aesKey, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
    return CryptoJS.enc.Utf8.stringify(decrypt);
}

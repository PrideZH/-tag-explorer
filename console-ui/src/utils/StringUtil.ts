export default class StringUtil {
	
  static isEmpty(str: string | null) {
	return str === null || str === undefined;
  }
  
  static isNotEmpty(str: string | null) {
	return !StringUtil.isEmpty(str);
  }

  static isBlank(str: string | null): boolean {
    if (str === null || str === undefined) return true;
    return str.trim() === '';
  }

  static isNotBlank(str: string | null): boolean {
    return !StringUtil.isBlank(str);
  }

}

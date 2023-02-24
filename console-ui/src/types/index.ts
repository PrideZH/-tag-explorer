export interface Page<T> {
  records: T[],
  total: number,
  pages: number
}

export interface BaseResp {
  id: string;
  createTime: Date;
  updateTime: Date;
}

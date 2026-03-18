export default class {
  amount: number;
  currency: string;
  receiptEmail: string | undefined;

  constructor(amount: number, currency: string, receiptEmail: string) {
    this.amount = amount;
    this.currency = currency;
    this.receiptEmail = receiptEmail;
  }
}

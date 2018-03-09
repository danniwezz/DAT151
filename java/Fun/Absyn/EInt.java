package Fun.Absyn; // Java Package generated by the BNF Converter.

public class EInt extends Exp {
  public final Integer integer_;
  public EInt(Integer p1) { integer_ = p1; }

  public <R,A> R accept(Fun.Absyn.Exp.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Fun.Absyn.EInt) {
      Fun.Absyn.EInt x = (Fun.Absyn.EInt)o;
      return this.integer_.equals(x.integer_);
    }
    return false;
  }

  public int hashCode() {
    return this.integer_.hashCode();
  }


}
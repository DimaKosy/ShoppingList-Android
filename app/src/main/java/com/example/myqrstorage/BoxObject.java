package com.example.myqrstorage;

public class BoxObject {
    public String Title;
    public int Amount;
    public Boolean Checked;
    public Boolean Updated;

    public BoxObject(String Title, int Amount){
        this.Title = Title;
        this.Amount = Amount;
        this.Checked = false;
        this.Updated = false;

    }

    public BoxObject(String Title, int Amount, boolean Checked){
        this.Title = Title;
        this.Amount = Amount;
        this.Checked = Checked;
        this.Updated = false;

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public Boolean getChecked() {
        return Checked;
    }

    public void setChecked(Boolean checked) {
        Checked = checked;
    }
}

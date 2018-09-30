package a15071894.coursework1.Friends;

//Friend object to be used for managing friends in database
public class Friend {
    private String name;
    private String phone;

    public Friend(String name, String phone){
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString(){
        return getName()+ ": "+getPhone();
    }
}

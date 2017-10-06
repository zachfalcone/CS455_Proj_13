public class HelloTeam {
    public static void main(String[] args) {
        HelloTeam helloTeam = new HelloTeam();
        helloTeam.start();
    }
    public void start() {
        System.out.println("Hello Team. Our team members are:");

        TeamMember zach = new TeamMember("Zach", "Programmer");
        zach.printInfo();

        // Add your names below

        TeamMember cesar = new TeamMember("Cesar", "QA");
        cesar.printInfo();
        
        TeamMember perry = new TeamMember("Perry", "Project Lead");
        perry.printInfo();
        
        TeamMember Sarjil = new TeamMember("Sarjil", "Design Architect");
        Sarjil.printInfo();
        
    }
    private class TeamMember {
        private String name, role;
        TeamMember(String name) {
            this.name = name;
            role = "Undecided";
        }
        TeamMember(String name, String role) {
            this.name = name;
            this.role = role;
        }
        void printInfo() {
            System.out.println(name + " - " + role + ".");
        }
    }
}

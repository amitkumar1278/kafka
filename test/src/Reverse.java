public class Reverse {


    public static void main(String[] args) {
        int [] arr = {10, 20, 30, 40, 50, 60};

        reverse(arr, arr.length);
    }

    private static void reverse(int[] arr, int length) {

        int temp;

        for(int i=0; i<length/2; i++){
            temp = arr[i];
            arr[i] = arr[length - i - 1];
            arr[length - i - 1] = temp;
        }

        System.out.println("Reversed: ");
        for(int j = 0; j<length; j++){
            System.out.println(arr[j]);
        }


    }


//    employee:
//    name,
//    id(PK),
//    sal:
//    managersemployeeID:
//
//    update employee set sal = sal + (Sal * .1)
//
//     1 Nakul 1000
//     2 Ankur 2000 1
//     3 Amit 3000 1
//
//
//    select e.id, e.name, e2.name
//    from employee e
//    inner join employee e2 on e2.id = e.managersemployeeID
//
//    2 Ankur Nakul;
//    3 Amit NAkul



}

package org.example.kuangstudythymeleaf;

import org.example.kuangstudythymeleaf.entity.Employee;
import org.example.kuangstudythymeleaf.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 测试数据生成器 - 生成 1000 条员工数据
 */
@SpringBootTest
public class DataGeneratorTest {

    @Autowired
    private EmployeeMapper employeeMapper;

    // 常见中文姓氏
    private static final String[] SURNAMES = {
        "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈",
        "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
        "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏",
        "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章"
    };

    // 常见中文名字
    private static final String[] GIVEN_NAMES = {
        "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "洋",
        "艳", "勇", "军", "杰", "娟", "涛", "明", "超", "秀兰", "霞",
        "平", "刚", "桂英", "华", "建", "玲", "辉", "峰", "俊", "毅",
        "欣", "雪", "鹏", "帅", "帆", "宇", "浩", "然", "博", "文"
    };

    // 部门 ID(1-5)
    private static final Integer[] DEPARTMENTS = {1, 2, 3, 4, 5};

    private final Random random = new Random();

    /**
     * 生成随机姓名
     */
    private String generateName() {
        String surname = SURNAMES[random.nextInt(SURNAMES.length)];
        String givenName1 = GIVEN_NAMES[random.nextInt(GIVEN_NAMES.length)];
        String givenName2 = GIVEN_NAMES[random.nextInt(GIVEN_NAMES.length)];
        return surname + givenName1 + (random.nextBoolean() ? givenName2 : "");
    }

    /**
     * 生成随机邮箱
     */
    private String generateEmail(String name, int id) {
        String[] domains = {"qq.com", "163.com", "gmail.com", "sina.com", "hotmail.com"};
        String domain = domains[random.nextInt(domains.length)];
        return name.replaceAll("\\s+", "") + id + "@" + domain;
    }

    /**
     * 生成随机日期 (1980-2005 年之间)
     */
    private LocalDate generateBirthDate() {
        int year = 1980 + random.nextInt(26);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28); // 简化处理，避免月末问题
        return LocalDate.of(year, month, day);
    }

    /**
     * 生成 1000 条员工数据并插入数据库
     */
    @Test
    public void generateAndInsertData() {
        List<Employee> employees = new ArrayList<>(1000);

        for (int i = 1; i <= 1000; i++) {
            Employee employee = new Employee();
            String name = generateName();
            employee.setLastName(name);
            employee.setEmail(generateEmail(name, i));
            employee.setGender(random.nextInt(2) + 1); // 1:男，2:女
            employee.setDepartment(DEPARTMENTS[random.nextInt(DEPARTMENTS.length)]);
            employee.setBirth(generateBirthDate());
            employees.add(employee);
        }

        // 批量插入
        int batchSize = 100;
        for (int i = 0; i < employees.size(); i += batchSize) {
            int end = Math.min(i + batchSize, employees.size());
            List<Employee> batch = employees.subList(i, end);
            
            for (Employee emp : batch) {
                employeeMapper.insert(emp);
            }
            
            System.out.println("已插入 " + end + " 条数据...");
        }

        System.out.println("✓ 成功插入 1000 条员工数据！");
    }
}

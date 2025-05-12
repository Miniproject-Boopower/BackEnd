import sys
import json

student_num = sys.argv[1]
password = sys.argv[2]

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
from datetime import datetime

chrome_options = Options()
chrome_options.add_argument("--headless")  # GUI 없이 실행하고 싶으면 이 줄 추가
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

driver.get('https://eclass.hufs.ac.kr/ilos/main/main_form.acl')

# 로그인 버튼 클릭
try:
    login_button = WebDriverWait(driver, 10).until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, 'li.header_login.login-btn-color'))
    )
    login_button.click()
except Exception as e:
    print('로그인 버튼 클릭 실패:', e)
    driver.quit()
    exit()

try:
    WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.NAME, 'usr_id')))
except:
    print("로그인 폼 로딩 실패")
    driver.quit()
    exit()

id_input = driver.find_element(By.NAME, 'usr_id')
pw_input = driver.find_element(By.NAME, 'usr_pwd')
id_input.send_keys(student_num)
pw_input.send_keys(password)

# --- 5. 로그인 완료 ---
login_submit_button = driver.find_element(By.CSS_SELECTOR, 'input[type="image"]')
login_submit_button.click()

time.sleep(3)

# --- 6. 정규/비정규 과목명 가져오기 ---
regular_courses = []
non_regular_courses = []
is_regular = True

li_elements = driver.find_elements(By.CSS_SELECTOR, 'ol > li')

for li in li_elements:
    if 'term_info' in li.get_attribute('class'):
        if li.text.strip() == '비정규과목':
            is_regular = False
        continue

    try:
        em_tag = li.find_element(By.CSS_SELECTOR, 'em.sub_open')
        title_attr = em_tag.get_attribute('title')
        if title_attr:
            course_name = title_attr.replace(' 강의실 들어가기', '').strip()
            if is_regular:
                regular_courses.append(course_name)
            else:
                non_regular_courses.append(course_name)
    except:
        continue

print(json.dumps({
    "regular": regular_courses,
    "non_regular": non_regular_courses
}, ensure_ascii=False))

# --- 10. 드라이버 종료 ---
driver.quit()
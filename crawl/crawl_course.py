import sys
import json
import shutil
import tempfile
import os
import time
from datetime import datetime
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait, Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager

student_num = sys.argv[1]
password = sys.argv[2]

chrome_options = Options()
chrome_options.add_argument("--headless")
chrome_options.add_argument("--lang=ko")
chrome_options.add_argument("--no-sandbox")
chrome_options.add_argument("--disable-dev-shm-usage")

user_data_dir = tempfile.mkdtemp()
chrome_options.add_argument(f"--user-data-dir={user_data_dir}")

driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

try:
    driver.get('https://eclass.hufs.ac.kr/ilos/main/main_form.acl')

    # 로그인 버튼 클릭
    WebDriverWait(driver, 10).until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, 'li.header_login.login-btn-color'))
    ).click()

    WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.NAME, 'usr_id')))

    id_input = driver.find_element(By.NAME, 'usr_id')
    pw_input = driver.find_element(By.NAME, 'usr_pwd')
    id_input.send_keys(student_num)
    pw_input.send_keys(password)

    driver.find_element(By.CSS_SELECTOR, 'input[type="image"]').click()

    # ✅ 로그인 후 언어 설정
    try:
        lang_select = Select(WebDriverWait(driver, 5).until(
            EC.presence_of_element_located((By.ID, "LANG"))
        ))
        lang_select.select_by_value("ko")
        # print("[DEBUG] 언어를 한국어로 설정 완료", file=sys.stderr)
        time.sleep(2)
    except Exception as e:
        print(f"[WARN] 언어 선택 실패: {e}", file=sys.stderr)

    time.sleep(3)
    # print("[DEBUG] 로그인 후 URL:", driver.current_url, file=sys.stderr)

    # --- 6. 정규/비정규 과목명 가져오기 ---
    regular_courses = []
    non_regular_courses = []
    is_regular = True

    li_elements = driver.find_elements(By.CSS_SELECTOR, 'ol > li')
    # print("[DEBUG] li 개수:", len(li_elements), file=sys.stderr)
    for li in li_elements:
        # print("[DEBUG] li text:", li.text.strip(), file=sys.stderr)
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

finally:
    # --- 10. 드라이버 종료 ---
    driver.quit()
    shutil.rmtree(user_data_dir, ignore_errors=True)
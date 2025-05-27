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

    # --- 7. 달력에서 과목일정(dot) 있는 날짜 추출 ---
    today = datetime.today()
    today_day = today.day

    schedule_days = []

    calendar_cells = driver.find_elements(By.CSS_SELECTOR, 'table.main-Schedule td')
    one_day_index = -1
    cell_day_list = []

    for idx, cell in enumerate(calendar_cells):
        try:
            day_text = cell.find_element(By.CSS_SELECTOR, 'div.day').text.strip()
            numeric_day = int(''.join(filter(str.isdigit, day_text)))
            cell_day_list.append((idx, numeric_day))
            if numeric_day == 1 and one_day_index == -1:
                one_day_index = idx
        except:
            cell_day_list.append((idx, -1))

    for idx, cell in enumerate(calendar_cells):
        if idx < one_day_index:
            continue
        try:
            day_text = cell.find_element(By.CSS_SELECTOR, 'div.day').text.strip()
            numeric_day = int(''.join(filter(str.isdigit, day_text)))
            has_dot = any(div.get_attribute('title') in ['과목일정', '개인일정']
                          for div in cell.find_elements(By.CSS_SELECTOR, 'div[title]'))
            if has_dot and numeric_day >= today_day:
                schedule_days.append(str(numeric_day))
        except:
            continue

    # print("[DEBUG] 달력 셀 수:", len(calendar_cells), file=sys.stderr)
    # print("[DEBUG] schedule_days:", schedule_days, file=sys.stderr)

    # --- 8. 일정 데이터 수집 시작 ---
    schedule_infos = []

    for day_text in schedule_days:
        try:
            calendar_cells = driver.find_elements(By.CSS_SELECTOR, 'table.main-Schedule td')
            clicked = False

            for cell in calendar_cells:
                try:
                    day = cell.find_element(By.CSS_SELECTOR, 'div.day').text.strip()
                    if day == day_text.replace('월', '').replace('일', '').strip():
                        cell.click()
                        WebDriverWait(driver, 10).until(
                            EC.presence_of_element_located((By.CSS_SELECTOR, '.schedule_txt_view div[id^="view_"]'))
                        )
                        clicked = True
                        break
                except:
                    continue

            if not clicked:
                continue

            task_elements = driver.find_elements(By.XPATH, '//*[starts-with(@id, "change_")]')
            # print("[DEBUG] task_elements 수:", len(task_elements), file=sys.stderr)

            for task_elem in task_elements:
                try:
                    task_id_full = task_elem.get_attribute('id')
                    parts = task_id_full.split('_')
                    index, source, kind, task_id = parts[1:5]

                    try:
                        show_button = driver.find_element(By.ID, f"show_{index}_{source}_{kind}_{task_id}")
                        if show_button.is_displayed():
                            show_button.click()
                            time.sleep(0.5)
                    except:
                        pass

                    view_id = f"view_{index}_{source}_{kind}_{task_id}"
                    detail_box = driver.find_element(By.ID, view_id)

                    if kind == '2':
                        task_name = task_elem.find_element(By.XPATH, f'//*[@id="{task_id_full}"]/div/span').text.strip()
                        subject_name = driver.find_element(By.XPATH, f'//*[@id="{view_id}"]/a/div[1]').text.strip()
                        deadline = driver.find_element(By.XPATH, f'//*[@id="{view_id}"]/a/div[2]').text.replace('마감일 :', '').strip()
                        status = next((div.text.replace('상태 :', '').strip()
                                       for div in detail_box.find_elements(By.CSS_SELECTOR, 'div') if '상태 :' in div.text), '')

                    elif kind == '3':
                        subject_name = detail_box.find_element(By.XPATH, f'//*[@id="{view_id}"]/div[1]').text.strip()
                        task_name = detail_box.find_element(By.XPATH, f'//*[@id="{view_id}"]/div[3]').text.strip()
                        try:
                            selected_day = int(day_text)
                            now = datetime.now()
                            deadline_dt = datetime(now.year, now.month, selected_day, 11, 30)
                            deadline = deadline_dt.strftime('%Y-%m-%d %H:%M')
                        except:
                            deadline = '개인일정'
                        status = '미제출'
                    else:
                        continue

                    schedule_infos.append({
                        '과제 제목': task_name,
                        '과목명': subject_name,
                        '마감일': deadline,
                        '상태': status
                    })

                except Exception as e:
                    print('일정 파싱 실패:', e, file=sys.stderr)
                    continue

        except Exception as e:
            print('날짜 클릭 실패:', e, file=sys.stderr)
            continue

    # print(f"[DEBUG] 수집된 과제 수: {len(schedule_infos)}", file=sys.stderr)
    print(json.dumps(schedule_infos, ensure_ascii=False))

finally:
    driver.quit()
    shutil.rmtree(user_data_dir, ignore_errors=True)
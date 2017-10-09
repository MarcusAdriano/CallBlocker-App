package com.call.blocker;

/*
 * Copyright 2017 Marcus Adriano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Contact {
    private int id;
    private String number;
    private String description;
    private boolean smsBlocker;
    private boolean callBlocker;

    public Contact () {

    }

    public Contact(String description, String number, boolean callBlocker, boolean smsBlocker) {
        this.description = description;
        this.number = number;
        this.smsBlocker = smsBlocker;
        this.callBlocker = callBlocker;
    }

    public boolean isSmsBlocker() {
        return smsBlocker;
    }

    public void setSmsBlocker(boolean smsBlocker) {
        this.smsBlocker = smsBlocker;
    }

    public boolean isCallBlocker() {
        return callBlocker;
    }

    public void setCallBlocker(boolean callBlocker) {
        this.callBlocker = callBlocker;
    }

    @Override
    public String toString() {
        return this.description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
